package l2mv.gameserver.inertiax.model;

import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__ASSIST_NO_TARGET;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__ATK_TARGET;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__CREDIT_END;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__END;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__FOLLOW_CLOSE;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__FOLLOW_FAR;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__NEW_TARGET;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__NO_TARGET;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__ON_KILL;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__REM_TARGET;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__START;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__WHILE_DEAD;
import static l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt.EVT__WHILE_TARGET_DEAD;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import l2mv.gameserver.inertiax.controller.InertiaController;
import l2mv.gameserver.inertiax.enums.EActionPriority;
import l2mv.gameserver.inertiax.enums.EAutoAttack;
import l2mv.gameserver.inertiax.enums.EMoveType;
import l2mv.gameserver.inertiax.enums.EPanelOption;
import l2mv.gameserver.inertiax.enums.ESearchType;
import l2mv.gameserver.inertiax.enums.ETargetType;
import l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt;
import l2mv.gameserver.inertiax.model.ext.InertiaExt;
import l2mv.gameserver.inertiax.model.filters.ActionFilter;
import l2mv.gameserver.inertiax.model.filters.AvailSkillActionFilter;
import l2mv.gameserver.inertiax.model.filters.TargetFilter;
import l2mv.gameserver.inertiax.model.filters.VTargetFilter;
import l2mv.gameserver.inertiax.model.panels.DropTracker;
import l2mv.gameserver.inertiax.model.panels.TargetFiltering;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.network.serverpackets.ExServerPrimitive;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.utils.Location;

/**
 * Inertia — core AutoFarm session per player.
 *
 * Ported from original inertiax (l2.ae.pvp) to MultVerso H5:
 *  - PlayerPassport → Player (looked up via World by objectId)
 *  - L2PcInstance  → Player
 *  - L2Character   → Creature
 *  - SkillTable.getInfo() → player.getKnownSkill(id) / player.getAllSkills()
 *  - GlobalRadar.getRadarDistance() → Creature.calcDistance()
 *  - Ghost check removed (no Ghost class in H5 inertiax build)
 *  - Util.clearArray() → Arrays.fill()
 *  - HTML paths updated to data/html-en/mods/autofarm/
 */
public class Inertia implements Callable<Inertia>
{
	// -------------------------------------------------------
	// Constants
	// -------------------------------------------------------
	/** Default credit: 4 hours in milliseconds */
	private static final long INIT_TICKS = 4L * 3_600_000L;

	// HTML templates
	private static final String STOPPED = "<td align=center><button value=\"Iniciar\" action=\"bypass inertia_start %d\" " + "width=70 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td align=center><font name=hs12 color=\"FF6363\">Parado</font></td>";

	private static final String RUNNING = "<td align=center><button value=\"Parar\" action=\"bypass inertia_stop %d\" " + "width=70 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td align=center><font name=hs12 color=\"63FF63\">Ativo</font></td>";

	private static final String ACTION_TEMPLATE = "<td align=center width=36><table height=34 cellspacing=0 cellpadding=0 background=%s>" + "<tr><td><table cellspacing=0 cellpadding=0><tr><td>" + "<button action=\"bypass inertia_action_edit %d %s\" width=34 height=34 " + "back=L2UI_CH3.menu_outline_Down fore=L2UI_CH3.menu_outline></td></tr>" + "</table></td></tr></table></td>";

	private static final String SEARCH_BTN = "<td align=center width=50><button value=\"%s\" action=\"bypass inertia_search_type %d %s\" " + "width=62 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";

	private static final String SEARCH_LBL = "<td align=center width=50><font name=hs12 color=\"%s\">%s</font></td>";

	// -------------------------------------------------------
	// Identity
	// -------------------------------------------------------
	/** Owner character objectId — used as the map key in InertiaController */
	private final int _ownerId;

	// -------------------------------------------------------
	// Sub-components
	// -------------------------------------------------------
	private final InertiaAct _inertiaAct = new InertiaAct(this);

	private final TargetFilter _targetFilter = new TargetFilter(this);
	private final VTargetFilter _vtargetFilter = new VTargetFilter(this);
	private final ActionFilter actionFilter = new ActionFilter(this);
	private final AvailSkillActionFilter availSkillFilter = new AvailSkillActionFilter(this);
	private final TargetComparator targetComparator = new TargetComparator();

	private final HashMap<EPanelOption, InertiaPanel> _panels = new HashMap<>();

	// -------------------------------------------------------
	// State
	// -------------------------------------------------------
	private boolean _running;
	private long _lagTicks;
	private long _remainingTicks;
	private EAutoAttack _autoAttack;
	private EMoveType _moveType;
	private ESearchType _searchType;
	private ETargetType _targetType;
	private int _searchRange;

	/** objectId of the party assist player (0 = unset) */
	private int _assistObjectId;
	private Location _lastSavedLocation;

	/** Up to 7 skill/action slots */
	private final InertiaCast[] _inertiaSkills = new InertiaCast[7];

	private boolean _exit;

	// -------------------------------------------------------
	// Constructor
	// -------------------------------------------------------
	/**
	 * @param player         The owner Player (used only to read objectId; may go offline)
	 * @param remainingTicks Stored credit from DB (0 → use default 4 h)
	 */
	public Inertia(final Player player, final long remainingTicks)
	{
		_ownerId = player.getObjectId();
		_remainingTicks = remainingTicks > 0 ? remainingTicks : INIT_TICKS;
		reset();
	}

	// -------------------------------------------------------
	// Callable interface
	// -------------------------------------------------------
	@Override
	public Inertia call()
	{
		if (_running)
		{
			tick(InertiaController.TICKS);
			tickEnd();
			_exit = false;
		}
		return this;
	}

	// -------------------------------------------------------
	// Player access (safe: may be offline)
	// -------------------------------------------------------
	/** Returns the online Player or null if offline. */
	public Player getActivePlayer()
	{
		return World.getPlayer(_ownerId);
	}

	/** Returns the current assist player or null. */
	public Player getAssistPlayer()
	{
		if (_assistObjectId == 0)
			return null;
		return World.getPlayer(_assistObjectId);
	}

	// -------------------------------------------------------
	// Session methods called by InertiaController
	// -------------------------------------------------------
	public InertiaAct getInertiaAct()
	{
		return _inertiaAct;
	}

	public InertiaCast setInertiaAction(int slot, int actionId, boolean isSkill)
	{
		if (slot < 0 || slot >= _inertiaSkills.length)
			return null;
		return _inertiaSkills[slot] = new InertiaCast(actionId, isSkill);
	}

	public InertiaCast getInertiaAction(int slot, final boolean isSkill)
	{
		if (slot < 0 || slot >= _inertiaSkills.length)
			return null;
		return _inertiaSkills[slot];
	}

	public boolean swapInertiaAction(final int slot0, final int slot1, final boolean isSkill)
	{
		if (slot0 < 0 || slot0 >= _inertiaSkills.length)
			return false;
		if (slot1 < 0 || slot1 >= _inertiaSkills.length)
			return false;
		final InertiaCast tmp = _inertiaSkills[slot0];
		_inertiaSkills[slot0] = _inertiaSkills[slot1];
		_inertiaSkills[slot1] = tmp;
		return true;
	}

	public void deleteInertiaAction(final int slot, final boolean isSkill)
	{
		if (slot >= 0 && slot < _inertiaSkills.length)
			_inertiaSkills[slot] = null;
	}

	public void addLag(final long newLag)
	{
		_lagTicks += newLag;
	}

	public void addCredit(final long ticks)
	{
		_remainingTicks += ticks;
	}

	public long getCredit()
	{
		return _remainingTicks;
	}

	public boolean isRunning()
	{
		return _running;
	}

	public void setRunning(final boolean running)
	{
		_running = running;
		renderRange();
	}

	public boolean exit()
	{
		return _exit;
	}

	public void raiseExit()
	{
		_exit = true;
	}

	public int getOwnerId()
	{
		return _ownerId;
	}

	public VTargetFilter getVTargetFilter()
	{
		return _vtargetFilter;
	}

	public ESearchType getSearchType()
	{
		return _searchType;
	}

	// -------------------------------------------------------
	// Mode setters
	// -------------------------------------------------------
	public void setAutoAttack(final EAutoAttack autoAttack)
	{
		if (autoAttack == _autoAttack)
			return;
		_autoAttack = autoAttack;
		final Player player = getActivePlayer();
		if (player != null)
			player.sendMessage("InertiaMode AttackType changed to -> [" + _autoAttack + "]");
	}

	public void setMoveType(EMoveType moveType)
	{
		final Player player = getActivePlayer();
		if (player == null)
			return;
		if (moveType == EMoveType.Current_Location)
		{
			_lastSavedLocation = new Location(player.getX(), player.getY(), player.getZ());
			player.sendMessage("Updated search location to current position.");
		}
		renderRange();
		if (moveType == _moveType)
			return;
		_moveType = moveType;
		player.sendMessage("InertiaMode MoveType changed to -> [" + _moveType + "]");
		renderRange();
	}

	public void setSearchTarget(final ESearchType searchType)
	{
		if (searchType == _searchType)
			return;
		_searchType = searchType;
		final Player player = getActivePlayer();
		if (player != null)
			player.sendMessage("InertiaMode SearchType changed to -> [" + _searchType + "]");
		renderRange();
	}

	public ETargetType getTargetType()
	{
		return _targetType;
	}

	public void setTargetType(final ETargetType targetType)
	{
		if (targetType == _targetType)
			return;
		_targetType = targetType;
		final Player player = getActivePlayer();
		if (player != null)
			player.sendMessage("InertiaMode TargetType changed to -> [" + _targetType + "]");
	}

	public int getSearchRange()
	{
		return _searchRange;
	}

	public void setSearchRange(final int searchRange)
	{
		if (searchRange == _searchRange)
			return;
		_searchRange = searchRange;
		final Player player = getActivePlayer();
		if (player != null)
			player.sendMessage("InertiaMode SearchRange changed to -> [" + _searchRange + "]");
		renderRange();
	}

	/**
	 * Set party assist target by player name.
	 * Used by InertiaController when the original PlayerPassport lookup is unavailable.
	 */
	public void setPartyTargetByName(final String name)
	{
		final Player player = getActivePlayer();
		if (player == null)
			return;

		if (name == null || name.equalsIgnoreCase("Not Set"))
		{
			_assistObjectId = 0;
			player.sendMessage("InertiaMode PartyTarget changed to -> UNSET");
			if (_moveType == EMoveType.Follow_Target)
				setMoveType(EMoveType.Not_Set);
			return;
		}

		final Player target = World.getPlayer(name);
		if (target == null)
		{
			player.sendMessage("Player not found: " + name);
			return;
		}

		if (target.getObjectId() == _ownerId)
			return;

		_assistObjectId = target.getObjectId();
		player.sendMessage("InertiaMode PartyTarget changed to -> [" + target.getName() + "]");
	}

	// -------------------------------------------------------
	// Reset
	// -------------------------------------------------------
	public void reset()
	{
		_running = false;
		_autoAttack = EAutoAttack.Mage;
		_targetType = ETargetType.MONSTRO;
		_searchRange = 600;
		_searchType = ESearchType.Near;
		_moveType = EMoveType.Not_Set;
		_assistObjectId = 0;
		_lastSavedLocation = null;
		Arrays.fill(_inertiaSkills, null);
		renderRange();
	}

	// -------------------------------------------------------
	// Tick logic
	// -------------------------------------------------------
	public void tick(final long ticks)
	{
		final Player player = getActivePlayer();
		if (player == null)
		{
			setRunning(false);
			return;
		}

		if (player.isDead())
		{
			evt(EVT__WHILE_DEAD);
			if (exit())
				return;
		}

		if (_remainingTicks - ticks < 0)
		{
			_remainingTicks = 0;
			evt(EVT__CREDIT_END);
			if (exit())
				return;
		}

		// Uncomment to enable credit consumption:
		// _remainingTicks -= ticks;

		if (_lagTicks > 0)
		{
			_lagTicks = Math.max(0, _lagTicks - ticks);
			return;
		}

		_inertiaAct.tick(ticks);
		_inertiaAct.evt(EVT__START);

		final Creature oldTargetCreature = (Creature) player.getTarget();
		if (oldTargetCreature != null && oldTargetCreature.isAlikeDead())
		{
			evt(EVT__REM_TARGET);
			return;
		}

		final Player assistPlayer = getAssistPlayer();
		if (assistPlayer != null && !assistPlayer.isInParty())
			_assistObjectId = 0;

		if (player.getParty() == null || _assistObjectId == 0)
		{
			boolean render = false;
			if (_moveType == EMoveType.Follow_Target)
			{
				setMoveType(EMoveType.Not_Set);
				render = true;
			}
			if (_searchType == ESearchType.Assist)
			{
				setSearchTarget(ESearchType.Off);
				render = true;
			}
			if (render)
				render();
		}

		final Creature currTarget = (Creature) player.getTarget();

		if (assistPlayer != null)
		{
			if (_moveType == EMoveType.Follow_Target && !player.isMoving())
			{
				if (player.getDistance(assistPlayer) <= 500 || player.isInCombat())
					evt(EVT__FOLLOW_CLOSE);
				else
					evt(EVT__FOLLOW_FAR);
			}
			if (currTarget == null)
				evt(EVT__ASSIST_NO_TARGET);
		}

		if (_searchType != ESearchType.Off)
		{
			if (_moveType == EMoveType.Not_Set)
				renderRange();

			if (currTarget != null && currTarget.isAlikeDead())
			{
				evt(EVT__WHILE_TARGET_DEAD);
			}
			else if (currTarget == null || currTarget == player || _searchType == ESearchType.Assist)
			{
				final Creature newTarget = searchTarget();
				if (newTarget != null && newTarget != currTarget)
				{
					_inertiaAct.setTarget(newTarget);
					evt(EVT__NEW_TARGET);
					return;
				}
			}
		}

		Creature actualTarget = (Creature) player.getTarget();
		if (actualTarget == null)
		{
			evt(EVT__NO_TARGET);
			actualTarget = _inertiaAct.getTarget();
		}

		if (actualTarget == null)
		{
			if (_moveType == EMoveType.Saved_Location)
			{
				final l2mv.gameserver.inertiax.model.panels.InertiaPath path = getSelectedPath();
				if (path != null && !path.getPoints().isEmpty())
				{
					Location targetWaypoint = path.getPoints().get(path.getCurrentWaypointIndex());
					double dist = player.getDistance(targetWaypoint.x, targetWaypoint.y);
					
					if (dist < 150.0)
					{
						path.nextWaypoint();
						player.sendMessage("Cleared area. Moving to waypoint " + (char)('A' + path.getCurrentWaypointIndex()));
						renderRange();
					}
					else if (!player.isMoving())
					{
						player.moveToLocation(targetWaypoint, 0, true);
					}
				}
			}
			return;
		}

		if (actualTarget.isAutoAttackable(player) && forceAutoAttack())
		{
			startAutoAttack(actualTarget);
			return;
		}
		else
		{
			final java.util.Optional<InertiaCast> avail = getAvailSkillActions().filter(availSkillFilter).findFirst();

			if (avail != null && avail.isPresent())
			{
				final InertiaCast availCast = avail.get();
				if (availCast != null)
				{
					final Skill availSkill = getSkill(availCast);
					if (availSkill != null)
					{
						if (availSkill.isOffensive() && !actualTarget.isAutoAttackable(player))
							player.setTarget(null);

						// Cast the skill on the current target
						player.doCast(availSkill, actualTarget, false);
						availCast.initReuse();
						return;
					}
				}
			}
			else if (_autoAttack == EAutoAttack.Melee || _autoAttack == EAutoAttack.Long_Range)
			{
				startAutoAttack(actualTarget);
			}
		}
	}

	public void tickEnd()
	{
		evt(EVT__END);
	}

	// -------------------------------------------------------
	// Helpers
	// -------------------------------------------------------
	public Skill getSkill(final InertiaCast inertiaCast)
	{
		final Player player = getActivePlayer();
		if (player == null)
			return null;
		final int skillId = inertiaCast.getActionId();
		final int skillLvl = player.getSkillLevel(skillId);
		if (skillLvl <= 0)
			return null;
		return player.getKnownSkill(skillId);
	}

	private boolean forceAutoAttack()
	{
		if ((_autoAttack == EAutoAttack.Melee || _autoAttack == EAutoAttack.Long_Range) && cantAction())
			return true;
		return false;
	}

	private boolean cantAction()
	{
		final Player player = getActivePlayer();
		if (player == null)
			return false;
		for (final InertiaCast is : _inertiaSkills)
		{
			if (is != null && !is.isUsableNow(player))
				return false;
		}
		return false;
	}

	private void startAutoAttack(final Creature target)
	{
		_inertiaAct.setTarget(target);
		evt(EVT__ATK_TARGET);
	}

	private Stream<InertiaCast> getAvailSkillActions()
	{
		return Stream.of(_inertiaSkills).filter(actionFilter);
	}

	// -------------------------------------------------------
	// Target searching
	// -------------------------------------------------------
	public Creature searchTarget()
	{
		if (_searchType == ESearchType.Assist)
			return getTargetByAssist();

		final Creature stored = _inertiaAct.getTarget();
		if (stored != null)
			return stored;

		return getTargetByRange(getSearchRange());
	}

	public Creature getTargetByAssist()
	{
		final Player assist = getAssistPlayer();
		if (assist == null)
			return null;
		return (Creature) assist.getTarget();
	}

	public Creature getTargetByRange(final int range)
	{
		final Player player = getActivePlayer();
		if (player == null)
			return null;
		final int searchRange = range > 0 ? range : _searchType.getRange();
		return player.getAroundCharacters(searchRange, 300).stream().filter(_targetFilter).sorted(targetComparator).findFirst().orElse(null);
	}

	public l2mv.gameserver.inertiax.model.panels.InertiaPath getSelectedPath()
	{
		l2mv.gameserver.inertiax.model.InertiaPanel panel = getPanel(EPanelOption.Path_Editor);
		if (panel instanceof l2mv.gameserver.inertiax.model.panels.PathEditor)
		{
			l2mv.gameserver.inertiax.model.panels.PathEditor pe = (l2mv.gameserver.inertiax.model.panels.PathEditor) panel;
			return pe.getSelePath();
		}
		return null;
	}

	/** The reference location used for range/path calculations. Returns a Location, never null if player is online. */
	public Location getSearchLocation()
	{
		if (_moveType == EMoveType.Saved_Location)
		{
			final l2mv.gameserver.inertiax.model.panels.InertiaPath path = getSelectedPath();
			if (path != null && !path.getPoints().isEmpty())
			{
				int idx = path.getCurrentWaypointIndex();
				if (idx >= 0 && idx < path.getPoints().size())
				{
					return path.getPoints().get(idx);
				}
			}
			if (_lastSavedLocation != null)
				return _lastSavedLocation;
		}
		else if (_moveType == EMoveType.Current_Location && _lastSavedLocation != null)
		{
			return _lastSavedLocation;
		}
		final Player p = getActivePlayer();
		return p != null ? p.getLoc() : null;
	}

	// -------------------------------------------------------
	// Target comparator (nearest first)
	// -------------------------------------------------------
	private class TargetComparator implements Comparator<Creature>
	{
		@Override
		public int compare(Creature o1, Creature o2)
		{
			final Player p = getActivePlayer();
			if (p == null)
				return 0;
			final Location loc = getSearchLocation();
			if (loc == null)
				return 0;
			final double d1 = Location.getDistance(loc, o1.getLoc());
			final double d2 = Location.getDistance(loc, o2.getLoc());
			return Double.compare(d1, d2);
		}
	}

	// -------------------------------------------------------
	// Events
	// -------------------------------------------------------
	private void evt(final EInertiaEvt inertiaEvt)
	{
		_inertiaAct.evt(inertiaEvt);
	}

	public void addInertiaExt(final InertiaExt inertiaExt)
	{
		_inertiaAct.addInertiaExt(inertiaExt);
	}

	public void onLogout()
	{
		_running = false;
	}

	public void onKill(final Creature victim)
	{
		evt(EVT__ON_KILL);
	}

	public void turnOff()
	{
		setRunning(false);
		render();
	}

	// -------------------------------------------------------
	// Panels
	// -------------------------------------------------------
	public InertiaPanel fetchPanel(final EPanelOption panelOption)
	{
		return _panels.computeIfAbsent(panelOption, p -> p.getCompute().apply(this));
	}

	public InertiaPanel getPanel(final EPanelOption panelOption)
	{
		return _panels.get(panelOption);
	}

	public void renderPanel(final EPanelOption panelOption, final Player viewer)
	{
		fetchPanel(panelOption).render(viewer);
	}

	public boolean toggleFilteredTarget(final Player viewer, final int npcTemplateId)
	{
		final InertiaPanel panel = fetchPanel(EPanelOption.Target_Filter);
		if (panel instanceof TargetFiltering)
		{
			TargetFiltering tf = (TargetFiltering) panel;
			return tf.toggleFilteredId(npcTemplateId);
		}
		return false;
	}

	public void onItemDrop(final int itemId, final long count)
	{
		if (_running)
		{
			final InertiaPanel p = fetchPanel(EPanelOption.Drops_Tracker);
			if (p instanceof DropTracker)
			{
				DropTracker dt = (DropTracker) p;
				dt.insertDrop(itemId, count);
			}
		}
	}

	// -------------------------------------------------------
	// Rendering
	// -------------------------------------------------------
	public void render(final Player viewer)
	{
		final Player player = getActivePlayer();
		if (player == null)
			return;

		final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		html.setFile("mods/autofarm/main.htm");

		html.replace("%state%", String.format(_running ? RUNNING : STOPPED, _ownerId));
		html.replace("%attack%", buildAutoAttack());
		html.replace("%target_type%", buildTargetType());
		html.replace("%range%", buildRange());
		html.replace("%move%", buildMoveType());
		html.replace("%time%", buildTime());
		html.replace("%ask%", buildActions(_inertiaSkills));
		html.replace("%id%", String.valueOf(_ownerId));
		html.replace("%name%", player.getName());

		viewer.sendPacket(html);
	}

	public void render()
	{
		final Player player = getActivePlayer();
		if (player != null)
			render(player);
	}

	public void renderActionEdit(final int slot, final int page)
	{
		final int SKILLS_PER_PAGE = 7;
		final Player player = getActivePlayer();
		if (player == null)
			return;

		final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		html.setFile("mods/autofarm/skill.htm");
		html.replace("%tit%", "Inertia Action " + slot);

		final List<Skill> availSkills = new ArrayList<>();
		for (final Skill skill : player.getAllSkills())
		{
			if (skill.isActive() && !skill.isToggle())
				availSkills.add(skill);
		}

		final int skillsLen = availSkills.size();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < SKILLS_PER_PAGE; i++)
		{
			final int indx = SKILLS_PER_PAGE * page + i;
			if (indx < skillsLen)
			{
				final Skill skill = availSkills.get(indx);
				sb.append(String.format(ACTION_TEMPLATE.replace("inertia_action_edit", "inertia_action_set"), skill.getIcon(), _ownerId, slot + " " + skill.getId()));
			}
		}
		html.replace("%ask%", sb.toString());

		final int pages = skillsLen < SKILLS_PER_PAGE ? 1 : skillsLen / SKILLS_PER_PAGE + (skillsLen % SKILLS_PER_PAGE > 0 ? 1 : 0);
		sb.setLength(0);
		for (int i = 0; i < pages; i++)
		{
			if (page == i)
				sb.append(String.format("<td align=center><font color=\"LEVEL\">[%d]</font></td>", i + 1));
			else
				sb.append(String.format("<td align=center><a action=\"bypass inertia_action_edit %d %d %d\">%d</a></td>", _ownerId, slot, i, i + 1));
		}
		html.replace("%pages1%", sb.toString());

		final InertiaCast action = slot >= 0 && slot < _inertiaSkills.length ? _inertiaSkills[slot] : null;
		if (action != null)
		{
			final Skill skill = getSkill(action);
			if (skill != null)
			{
				html.replace("%sic%", skill.getIcon());
				html.replace("%sna%", skill.getName());
			}
			else
			{
				html.replace("%sic%", "L2UI_CT1.Inventory_DF_CloakSlot_Disable");
				html.replace("%sna%", "Unknown");
			}
			html.replace("%reu%", String.format("%.2fs", action.getReuse()));
			html.replace("%hpp%", String.format("%05.2f%%", action.getUserHp()));
			html.replace("%tpp%", String.format("%05.2f%%", action.getTargetHp()));

			final EActionPriority[] epriorities = EActionPriority.values();
			final EActionPriority priority = slot < epriorities.length ? epriorities[slot] : epriorities[0];
			final StringBuilder spr = new StringBuilder(priority.toString());
			for (final EActionPriority pr : epriorities)
				if (pr != priority)
					spr.append(";").append(pr);
			html.replace("%pr%", spr.toString());
		}
		else
		{
			html.replace("%sic%", "L2UI_CT1.Inventory_DF_CloakSlot_Disable");
			html.replace("%sna%", "Empty");
			html.replace("%reu%", "?");
			html.replace("%hpp%", "?");
			html.replace("%tpp%", "?");
			html.replace("%pr%", "");
		}

		html.replace("%priority%", String.valueOf(slot + 1));
		html.replace("%slot%", String.valueOf(slot));
		html.replace("%id%", String.valueOf(_ownerId));
		player.sendPacket(html);
	}

	private void renderRange()
	{
		final Player player = getActivePlayer();
		if (player == null)
			return;

		final int searchRange = getSearchRange();
		final Location renderLoc = _moveType == EMoveType.Saved_Location && _lastSavedLocation != null ? _lastSavedLocation : player.getLoc();

		final ExServerPrimitive prim = new ExServerPrimitive("SearchRange", renderLoc.getX(), renderLoc.getY(), renderLoc.getZ());

		if (_running && _moveType != EMoveType.Follow_Target && searchRange > 1)
		{
			prim.addCircle(Color.GREEN, searchRange, 30, -20);
			prim.addCircle(Color.GREEN, 5, 4, -20);
		}
		player.sendPacket(prim);
	}

	// -------------------------------------------------------
	// HTML builders
	// -------------------------------------------------------
	private String buildTime()
	{
		final long hours = _remainingTicks / 3_600_000;
		final long minutes = (_remainingTicks % 3_600_000) / 60_000;
		final long seconds = (_remainingTicks % 60_000) / 1000;
		return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
	}

	private String buildAutoAttack()
	{
		final StringBuilder sb = new StringBuilder(_autoAttack.toString());
		for (final EAutoAttack at : EAutoAttack.values())
			if (at != _autoAttack)
				sb.append(";").append(at);
		return sb.toString();
	}

	private String buildTargetType()
	{
		final StringBuilder sb = new StringBuilder(_targetType.toString());
		for (final ETargetType tt : ETargetType.values())
			if (tt != _targetType)
				sb.append(";").append(tt);
		return sb.toString();
	}

	private String buildRange()
	{
		final List<Integer> ranges = Arrays.asList(300, 500, 600, 800, 1000, 1200, 1500, 1800, 2200);
		final StringBuilder sb = new StringBuilder(String.valueOf(_searchRange));
		for (final int r : ranges)
			if (r != _searchRange)
				sb.append(";").append(r);
		return sb.toString();
	}

	private String buildMoveType()
	{
		final StringBuilder sb = new StringBuilder(_moveType.toString());
		for (final EMoveType mt : EMoveType.values())
		{
			if (mt == _moveType)
				continue;
			if (mt == EMoveType.Saved_Location && _lastSavedLocation == null)
				continue;
			if (mt == EMoveType.Follow_Target && _assistObjectId == 0)
				continue;
			sb.append(";").append(mt);
		}
		// Replace "Target" label with the assist player's name if set
		if (_assistObjectId != 0)
		{
			final Player assist = getAssistPlayer();
			if (assist != null)
				return sb.toString().replace("Target", assist.getName());
		}
		return sb.toString();
	}

	private String buildParty()
	{
		final Player player = getActivePlayer();
		if (player == null)
			return "Not Set";
		final var party = player.getParty();
		if (party == null)
			return "Not Set";

		String assistName = "Not Set";
		if (_assistObjectId != 0)
		{
			final Player assist = getAssistPlayer();
			if (assist != null)
				assistName = assist.getName();
		}

		final StringBuilder sb = new StringBuilder(assistName).append(";Not Set");
		for (final Player member : party.getMembers())
		{
			if (member.getObjectId() != _assistObjectId && member.getObjectId() != _ownerId)
				sb.append(";").append(member.getName());
		}
		return sb.toString();
	}

	private String buildSearch()
	{
		final StringBuilder sb = new StringBuilder(512);
		for (final ESearchType et : ESearchType.values())
		{
			if (et == _searchType)
				sb.append(String.format(SEARCH_LBL, et.getColor(), et.toString()));
			else
				sb.append(String.format(SEARCH_BTN, et.toString(), _ownerId, et.toString()));
		}
		return sb.toString();
	}

	private String buildOptions()
	{
		final StringBuilder sb = new StringBuilder();
		for (final EPanelOption opt : EPanelOption.values())
			sb.append(opt.toString()).append(";");
		return sb.toString();
	}

	private String buildActions(final InertiaCast[] actions)
	{
		final StringBuilder sb = new StringBuilder(1024);
		int aid = 0;
		for (final InertiaCast action : actions)
		{
			if (action != null)
				sb.append(String.format(ACTION_TEMPLATE, action.getIcon(), _ownerId, String.valueOf(aid++)));
			else
				sb.append(String.format(ACTION_TEMPLATE, "L2UI_CT1.Inventory_DF_CloakSlot_Disable", _ownerId, String.valueOf(aid++)));
		}
		return sb.toString();
	}
}
