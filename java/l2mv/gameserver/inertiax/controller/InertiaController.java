package l2mv.gameserver.inertiax.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.handler.bypass.BypassHandler;
import l2mv.gameserver.handler.bypass.IBypassHandler;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2mv.gameserver.inertiax.enums.EActionPriority;
import l2mv.gameserver.inertiax.enums.EAutoAttack;
import l2mv.gameserver.inertiax.enums.EMoveType;
import l2mv.gameserver.inertiax.enums.EPanelOption;
import l2mv.gameserver.inertiax.enums.ESearchType;
import l2mv.gameserver.inertiax.enums.ETargetType;
import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * InertiaController — Ported from InertiaX to MultVerso H5 architecture.
 *
 * Key changes from the original:
 *  - PlayerPassport replaced by Player / objectId (int)
 *  - L2DatabaseFactory replaced by DatabaseFactory.getInstance().getConnection()
 *  - IBypassHandler uses H5's l2mv.gameserver.handler.bypass.IBypassHandler
 *  - IVoicedCommandHandler used to register the ".autofarm" voiced command
 *  - Shutdown integration uses Runtime.getRuntime().addShutdownHook()
 */
public class InertiaController implements IBypassHandler, IVoicedCommandHandler
{
	// -------------------------------------------------------
	// Threading
	// -------------------------------------------------------
	public static long TICKS = 800;

	private static final ScheduledExecutorService INERTIA_MAIN = Executors.newSingleThreadScheduledExecutor(r ->
	{
		Thread t = new Thread(r, "INERTIAX Main");
		t.setDaemon(true);
		return t;
	});

	private static final ThreadPoolExecutor INERTIA_POOL = new ThreadPoolExecutor(4, 6, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r ->
	{
		Thread t = new Thread(r, "INERTIAX Pool");
		t.setPriority(Thread.NORM_PRIORITY);
		t.setDaemon(true);
		return t;
	});

	// -------------------------------------------------------
	// State
	// -------------------------------------------------------
	/** objectId -> Inertia session */
	private final ConcurrentHashMap<Integer, Inertia> _playerInertias = new ConcurrentHashMap<>();

	/** objectId -> stored credit */
	private final ConcurrentHashMap<Integer, Long> _playerCredit = new ConcurrentHashMap<>();

	private final InertiaTask _inertiaTask = new InertiaTask();

	// -------------------------------------------------------
	// Bypass / voiced command tokens
	// -------------------------------------------------------
	private static final String[] BYPASSES =
	{
		"inertia",
		"inertia_start",
		"inertia_stop",
		"inertia_reset",
		"inertia_refresh",
		"inertia_main",
		"inertia_attack_type",
		"inertia_move_type",
		"inertia_search_type",
		"inertia_party_target",
		"inertia_action_edit",
		"inertia_action_set",
		"inertia_reuse_set",
		"inertia_hpp_set",
		"inertia_tpp_set",
		"inertia_slot_set",
		"inertia_render_panel",
		"inertia_panel_target_filter",
		"inertia_panel_drops_tracker",
		"inertia_panel_range_editor",
		"inertia_panel_path_editor",
		"inertia_target_type",
		"inertia_range_set",
		"inertia_buy_credits_page",
		"inertia_buy_credits",
		"inertia_start_path"
	};
	private static final String[] VOICE_COMMANDS =
	{
		"autofarm"
	};

	// -------------------------------------------------------
	// Singleton
	// -------------------------------------------------------
	private static class InstanceHolder
	{
		private static final InertiaController _instance = new InertiaController();
	}

	public static InertiaController getInstance()
	{
		return InstanceHolder._instance;
	}

	private InertiaController()
	{
		// Register bypass and voiced command
		BypassHandler.getInstance().registerBypass(this);
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);

		// Save credits on JVM shutdown
		Runtime.getRuntime().addShutdownHook(new Thread(this::storeMe, "INERTIAX Shutdown Hook"));

		// Warm up thread pool
		_inertiaTask.execute(1000);

		// Load stored credits from DB
		load();
	}

	// -------------------------------------------------------
	// Task control
	// -------------------------------------------------------
	public void stop()
	{
		_inertiaTask.stop();
	}

	public void start()
	{
		_inertiaTask.start();
	}

	public boolean isRunning()
	{
		return _inertiaTask.isRunning();
	}

	public void setTicks(final long ticks)
	{
		TICKS = ticks;
	}

	public long getLag()
	{
		return _inertiaTask.getLag();
	}

	// -------------------------------------------------------
	// Inner tick task
	// -------------------------------------------------------
	private class InertiaTask implements Runnable
	{
		private long _lag;
		private boolean _running = true;
		private final List<Callable<Inertia>> callables = new ArrayList<>(100);

		@Override
		public void run()
		{
			final long t0 = System.nanoTime();

			callables.addAll(_playerInertias.values());

			if (!callables.isEmpty())
			{
				try
				{
					INERTIA_POOL.invokeAll(callables, TICKS, TimeUnit.MILLISECONDS);
					callables.clear();
				}
				catch (InterruptedException e)
				{
					stop();
					e.printStackTrace();
				}
			}

			_lag = System.nanoTime() - t0;
			final long delay = TICKS - TimeUnit.NANOSECONDS.toMillis(_lag);
			execute(Math.max(0, delay));
		}

		public long getLag()
		{
			return _lag;
		}

		public void execute(final long delay)
		{
			if (isRunning())
				INERTIA_MAIN.schedule(this, delay, TimeUnit.MILLISECONDS);
		}

		public void stop()
		{
			_running = false;
		}

		public void start()
		{
			if (_running)
				throw new RuntimeException("InertiaTask already running!");
			_running = true;
			execute(0);
		}

		public boolean isRunning()
		{
			return _running;
		}
	}

	// -------------------------------------------------------
	// HTML strings for admin panel
	// -------------------------------------------------------
	private static final String SET_ON = "<tr><td><font name=hs12 color=\"LEVEL\">InertiaX Core</font></td>" + "<td align=center><font name=hs12 color=\"63FF63\">Active</font></td>" + "<td align=center><button value=\"Shutdown\" action=\"bypass admin_inertia_shutdown\" " + "width=80 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>";

	private static final String SET_OF = "<tr><td><font name=hs12 color=\"LEVEL\">InertiaX Core</font></td>" + "<td align=center><button value=\"Active\" action=\"bypass admin_inertia_activate\" " + "width=80 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td align=center><font name=hs12 color=\"FF6363\">Shutdown</font></td></tr>";

	// -------------------------------------------------------
	// Admin panel render
	// -------------------------------------------------------
	public void adminPanel(final Player player)
	{
		final NpcHtmlMessage npcHtml = new NpcHtmlMessage(player.getObjectId());
		npcHtml.setFile("mods/autofarm/admin.htm");

		npcHtml.replace("%state%", isRunning() ? SET_ON : SET_OF);

		final List<Long> list = new ArrayList<>(Arrays.asList(300L, 500L, 600L, 800L, 900L, 1300L, 1600L, 1800L, 2200L, 2600L, 3300L, 3800L, 4400L, 5500L, 6200L));
		list.remove(Long.valueOf(TICKS));

		StringBuilder ticks = new StringBuilder(String.valueOf(TICKS));
		for (final Long tick : list)
			ticks.append(";").append(tick);

		npcHtml.replace("%ticks%", ticks.toString());
		npcHtml.replace("%lag%", String.format("%.02f", getLag() / 1_000_000d));
		npcHtml.replace("%count%", String.format("%d / %d", _playerInertias.values().stream().filter(Inertia::isRunning).count(), _playerInertias.size()));

		player.sendPacket(npcHtml);
	}

	// -------------------------------------------------------
	// Inertia session management
	// -------------------------------------------------------
	/**
	 * Fetch (or create) the Inertia session for the given player.
	 */
	public Inertia fetchInertia(final Player player)
	{
		final int objectId = player.getObjectId();

		return _playerInertias.computeIfAbsent(objectId, id ->
		{
			final long credit = _playerCredit.getOrDefault(id, 0L);
			final Inertia inertia = new Inertia(player, credit);
			inertia.addInertiaExt(new l2mv.gameserver.inertiax.model.ext.PlayerExt());
			return inertia;
		});
	}

	/**
	 * Find inertia — if viewer is a GM and viewId differs, return that player's session; else return the active player's session.
	 */
	public Inertia findInertia(final Player player, final int viewId)
	{
		if (player.getObjectId() != viewId && player.isGM())
		{
			// GM viewing another player's session — fetch by objectId directly
			Inertia viewed = _playerInertias.get(viewId);
			if (viewed != null)
				return viewed;
		}
		return fetchInertia(player);
	}

	/**
	 * Return existing Inertia session or null if not loaded.
	 */
	public Inertia getInertia(final Player player)
	{
		return _playerInertias.get(player.getObjectId());
	}

	/** Render the autofarm main UI for the player. */
	public void renderInertia(final Player player)
	{
		final Inertia inertia = fetchInertia(player);
		inertia.render(player);
	}

	// -------------------------------------------------------
	// IBypassHandler — handles "inertia ..." bypasses
	// -------------------------------------------------------
	@Override
	public String[] getBypasses()
	{
		return BYPASSES;
	}

	@Override
	public void onBypassFeedback(final NpcInstance npc, final Player player, final String cmd)
	{
		if (!cmd.contains("inertia") || cmd.contains("admin"))
			return;

		final StringTokenizer st = new StringTokenizer(cmd);
		st.nextToken(); // skip "inertia"

		if (!st.hasMoreTokens())
			return;

		final int viewId;
		try
		{
			viewId = Integer.parseInt(st.nextToken());
		}
		catch (NumberFormatException e)
		{
			return;
		}

		final Inertia inertia = findInertia(player, viewId);

		if (cmd.startsWith("inertia_start_path"))
		{
			if (st.hasMoreTokens())
			{
				String mode = st.nextToken();
				if ("line".equalsIgnoreCase(mode))
				{
					inertia.setMoveType(EMoveType.Saved_Location);
					inertia.setRunning(true);
					player.sendMessage("Autofarm iniciado seguindo a linha do caminho.");
				}
				else if ("free".equalsIgnoreCase(mode))
				{
					inertia.setMoveType(EMoveType.Current_Location);
					inertia.setRunning(true);
					player.sendMessage("Autofarm iniciado com farm livre.");
				}
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_start"))
		{
			final l2mv.gameserver.inertiax.model.panels.InertiaPath path = inertia.getSelectedPath();
			if (path != null && !path.getPoints().isEmpty())
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
				html.setFile("mods/autofarm/ask_path.htm");
				html.replace("%id%", String.valueOf(viewId));
				player.sendPacket(html);
			}
			else
			{
				inertia.setRunning(true);
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_stop"))
		{
			inertia.setRunning(false);
			inertia.render(player);
		}
		else if (cmd.startsWith("inertia_reset"))
		{
			inertia.reset();
			inertia.render(player);
		}
		else if (cmd.startsWith("inertia_refresh") || cmd.startsWith("inertia_main"))
		{
			inertia.render(player);
		}
		else if (cmd.startsWith("inertia_attack_type"))
		{
			if (st.hasMoreTokens())
			{
				String strType = st.nextToken();
				while (st.hasMoreTokens())
					strType += "_" + st.nextToken();
				final EAutoAttack attackType = Enum.valueOf(EAutoAttack.class, strType);
				inertia.setAutoAttack(attackType);
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_move_type"))
		{
			if (st.hasMoreTokens())
			{
				String strType = st.nextToken();
				while (st.hasMoreTokens())
					strType += "_" + st.nextToken();
				final EMoveType moveType = strType.contains("Follow") ? EMoveType.Follow_Target : Enum.valueOf(EMoveType.class, strType);
				inertia.setMoveType(moveType);
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_search_type"))
		{
			if (st.hasMoreTokens())
			{
				final ESearchType searchType = Enum.valueOf(ESearchType.class, st.nextToken());
				inertia.setSearchTarget(searchType);
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_party_target"))
		{
			if (st.hasMoreTokens())
			{
				String name = st.nextToken();
				while (st.hasMoreTokens())
					name += "_" + st.nextToken();

				inertia.setPartyTargetByName(name);
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_action_edit"))
		{
			final int slot = Integer.parseInt(st.nextToken());
			int page = 0;
			if (st.hasMoreTokens())
				page = Integer.parseInt(st.nextToken());
			inertia.renderActionEdit(slot, page);
		}
		else if (cmd.startsWith("inertia_action_set"))
		{
			if (st.hasMoreTokens())
			{
				final int slot = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					final int acid = Integer.parseInt(st.nextToken());
					inertia.setInertiaAction(slot, acid, true);
					inertia.renderActionEdit(slot, 0);
				}
			}
		}
		else if (cmd.startsWith("inertia_reuse_set"))
		{
			if (st.hasMoreTokens())
			{
				final int slot = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					final double reus = Double.parseDouble(st.nextToken());
					final var action = inertia.getInertiaAction(slot, true);
					if (action != null)
						action.setReuse(reus);
					inertia.renderActionEdit(slot, 0);
				}
			}
		}
		else if (cmd.startsWith("inertia_hpp_set"))
		{
			if (st.hasMoreTokens())
			{
				final int slot = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					final double userHp = Double.parseDouble(st.nextToken());
					final var action = inertia.getInertiaAction(slot, true);
					if (action != null)
						action.setUserHP(userHp);
					inertia.renderActionEdit(slot, 0);
				}
			}
		}
		else if (cmd.startsWith("inertia_tpp_set"))
		{
			if (st.hasMoreTokens())
			{
				final int slot = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					final double targHp = Double.parseDouble(st.nextToken());
					final var action = inertia.getInertiaAction(slot, true);
					if (action != null)
						action.setTargetHP(targHp);
					inertia.renderActionEdit(slot, 0);
				}
			}
		}
		else if (cmd.startsWith("inertia_slot_set"))
		{
			if (st.hasMoreTokens())
			{
				final int slot0 = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					final int slot1 = Integer.parseInt(st.nextToken()) - 1;
					final var action = inertia.getInertiaAction(slot0, true);
					if (action != null)
					{
						final EActionPriority newPriority = EActionPriority.values()[slot1];
						if (newPriority == EActionPriority.Remove)
						{
							inertia.deleteInertiaAction(slot0, action.isSkill());
							inertia.render(player);
						}
						else if (inertia.swapInertiaAction(slot0, slot1, action.isSkill()))
						{
							inertia.renderActionEdit(slot1, 0);
						}
					}
				}
			}
		}
		else if (cmd.startsWith("inertia_render_panel"))
		{
			if (st.hasMoreTokens())
			{
				final int ord = Integer.parseInt(st.nextToken()) - 1;
				final EPanelOption[] panelOptions = EPanelOption.values();
				if (ord < panelOptions.length)
					panelOptions[ord].render(inertia, player);
			}
		}
		else if (cmd.startsWith("inertia_target_type"))
		{
			if (st.hasMoreTokens())
			{
				String strType = st.nextToken();
				while (st.hasMoreTokens())
					strType += "_" + st.nextToken();
				final ETargetType targetType = Enum.valueOf(ETargetType.class, strType);
				inertia.setTargetType(targetType);
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_range_set"))
		{
			if (st.hasMoreTokens())
			{
				final int range = Integer.parseInt(st.nextToken());
				inertia.setSearchRange(range);
				inertia.render(player);
			}
		}
		else if (cmd.startsWith("inertia_buy_credits_page"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
			html.setFile("mods/autofarm/buycredits.htm");
			html.replace("%id%", String.valueOf(viewId));
			html.replace("%name%", player.getName());
			player.sendPacket(html);
		}
		else if (cmd.startsWith("inertia_buy_credits"))
		{
			if (st.hasMoreTokens())
			{
				final int hours = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					final int itemId = Integer.parseInt(st.nextToken());
					if (st.hasMoreTokens())
					{
						final long price = Long.parseLong(st.nextToken());
						
						// Check if player has the items
						if (player.getInventory().getCountOf(itemId) < price)
						{
							player.sendMessage("Você não possui a quantidade necessária de itens para esta compra.");
							return;
						}
						
						// Deduct items
						player.getInventory().destroyItemByItemId(itemId, price, "Autofarm Buy Credits");
						
						// Add credits
						final long addMs = hours * 3_600_000L;
						inertia.addCredit(addMs);
						
						// Save credits immediately to DB
						storeMe();
						
						player.sendMessage("Você comprou " + hours + " horas de autofarm com sucesso!");
						inertia.render(player);
					}
				}
			}
		}
		else if (cmd.startsWith("inertia_panel"))
		{
			for (final EPanelOption panelOption : EPanelOption.values())
			{
				final String cmdeq = String.format("inertia_panel_%s", panelOption.toLowerCase());
				if (cmd.startsWith(cmdeq))
				{
					final var inertiaPanel = inertia.fetchPanel(panelOption);
					inertiaPanel.onBypass(player, st);
					break;
				}
			}
		}
	}

	// -------------------------------------------------------
	// IVoicedCommandHandler — ".autofarm" command
	// -------------------------------------------------------
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICE_COMMANDS;
	}

	@Override
	public boolean useVoicedCommand(final String command, final Player player, final String target)
	{
		if (player == null)
			return false;

		if ("autofarm".equals(command))
		{
			renderInertia(player);
			return true;
		}
		return false;
	}

	// -------------------------------------------------------
	// DB persistence
	// -------------------------------------------------------
	public boolean storeMe()
	{
		final long t0 = System.currentTimeMillis();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement pst = con.prepareStatement("INSERT INTO character_inertia_credit (owner_id, credits) VALUES (?, ?) " + "ON DUPLICATE KEY UPDATE credits = ?"))
		{
			con.setAutoCommit(false);

			for (final var entry : _playerInertias.entrySet())
			{
				final int ownerId = entry.getKey();
				final Inertia inertia = entry.getValue();

				pst.setInt(1, ownerId);
				pst.setLong(2, inertia.getCredit());
				pst.setLong(3, inertia.getCredit());
				pst.addBatch();
			}

			final int total = pst.executeBatch().length;
			con.commit();

			System.err.printf("[InertiaX] Saved %d player credit(s) in %d ms.%n", total, System.currentTimeMillis() - t0);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void load()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement createTable = con.prepareStatement(
				"CREATE TABLE IF NOT EXISTS `character_inertia_credit` (" +
				"  `owner_id` INT NOT NULL," +
				"  `credits` BIGINT NOT NULL DEFAULT 0," +
				"  PRIMARY KEY (`owner_id`)" +
				") ENGINE=InnoDB"))
			{
				createTable.executeUpdate();
			}

			try (PreparedStatement st = con.prepareStatement("SELECT * FROM character_inertia_credit"); ResultSet rs = st.executeQuery())
			{
				while (rs.next())
				{
					final int ownerId = rs.getInt("owner_id");
					final long credit = rs.getLong("credits");
					_playerCredit.put(ownerId, credit);
				}
				System.out.println("[InertiaX] Loaded " + _playerCredit.size() + " player credit record(s).");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
