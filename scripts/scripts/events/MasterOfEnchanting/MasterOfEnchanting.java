package events.MasterOfEnchanting;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.gameserver.Announcements;
import premium.gameserver.listener.actor.player.OnPlayerEnterListener;
import premium.gameserver.model.Player;
import premium.gameserver.model.SimpleSpawner;
import premium.gameserver.model.actor.listener.CharListenerList;
import premium.gameserver.scripts.Functions;
import premium.gameserver.scripts.ScriptFile;

/**
 * Autor: Bonux Date: 30.08.09 Time: 17:49 http://www.lineage2.com/archive/2009/06/master_of_encha.html
 **/
public class MasterOfEnchanting extends Functions implements ScriptFile, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(MasterOfEnchanting.class);
	private static final String EVENT_NAME = "MasterOfEnchanting";
	private static int EVENT_MANAGER_ID = 32599;
	private static List<SimpleSpawner> _spawns = new ArrayList<>();
	private static boolean _active = false;
	@SuppressWarnings("unused")
	private static final int[][] _herbdrop =
	{
		{
			20000,
			100
		}, // Spicy Kimchee
		{
			20001,
			100
		}, // Spicy Kimchee
		{
			20002,
			100
		}, // Spicy Kimchee
		{
			20003,
			100
		}
	};  
	 
	public static final int[][] _energydrop =
	{
		{
			20004,
			30
		}, // Energy Ginseng
		{
			20005,
			100
		}
	}; // Energy Red Ginseng
	
 
	public void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] =
		{
			{
				-119494,
				44882,
				360,
				24576
			}, // Kamael Village
			{
				86865,
				-142915,
				-1336,
				26000
			}
		};
		
		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}
	
 
	public void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}
	
 
	private static boolean isActive()
	{
		return IsActive(EVENT_NAME);
	}
	 
	public void startEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}
		
		if (SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			System.out.println("Event: Master of Enchanting started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MasOfEnch.AnnounceEventStarted", null);
		}
		else
		{
			player.sendMessage("Event 'Master of Enchanting' already started.");
		}
		
		_active = true;
		show("admin/events/events.htm", player);
	}
	
	/**
	 * ????????????? ?????
	 */
	public void stopEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}
		if (SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			System.out.println("Event: Master of Enchanting stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MasOfEnch.AnnounceEventStoped", null);
		}
		else
		{
			player.sendMessage("Event 'Master of Enchanting' not started.");
		}
		
		_active = false;
		show("html/admin/events/events.htm", player);
	}
	
	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Master of Enchanting [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: Master of Enchanting [state: deactivated]");
		}
	}
	
	@Override
	public void onReload()
	{
		unSpawnEventManagers();
	}
	
	@Override
	public void onShutdown()
	{
		unSpawnEventManagers();
	}
	
	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.MasOfEnch.AnnounceEventStarted", null);
		}
	}
	
	// TODO: ???? ??????????? ????????? ????? ? ?? ????
	/**
	 * ?????????? ?????? ?????, ??????????? ????????? ??????
	 */
	/**
	 * public static void onDeath(L2Character cha, L2Character killer) { if (_active && cha.isMonster && !cha.isRaid && killer != null && killer.getPlayer() != null && Math.abs(cha.getLevel() - killer.getLevel()) < 10) { for (int[] drop : _herbdrop) if (Rnd.get(1000) <= drop[1]) { L2ItemInstance
	 * item = ItemTable.getInstance().createItem(drop[0], killer.getPlayer().getObjectId(), 0, "Master of Enchanting"); ((L2NpcInstance) cha).dropItem(killer.getPlayer(), item); break; } for (int[] drop : _energydrop) if (Rnd.get(1000) <= drop[1]) { L2ItemInstance item =
	 * ItemTable.getInstance().createItem(drop[0], killer.getPlayer().getObjectId(), 0, "Master of Enchanting"); ((L2NpcInstance) cha).dropItem(killer.getPlayer(), item); break; } } }
	 **/
}