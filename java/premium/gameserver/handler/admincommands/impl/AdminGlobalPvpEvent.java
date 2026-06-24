package premium.gameserver.handler.admincommands.impl;

import premium.gameserver.handler.admincommands.IAdminCommandHandler;
import premium.gameserver.instancemanager.ReflectionManager;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;
import premium.gameserver.model.Zone;
import premium.gameserver.taskmanager.GlobalPvPZoneTaskManager;

public class AdminGlobalPvpEvent implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_start_global_pvp_event,
		admin_stop_global_pvp_event
	}
	
	@Override
	public boolean useAdminCommand(@SuppressWarnings("rawtypes") Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		
		switch (command)
		{
			case admin_start_global_pvp_event:
			{
				for (Zone zone : ReflectionManager.DEFAULT.getZones())
				{
					if (zone.getType() == Zone.ZoneType.global_pvp_zone)
					{
						zone.setActive(true);
					}
				}
				
				for (Player player : GameObjectsStorage.getAllPlayersCopy())
				{
					GlobalPvPZoneTaskManager.getInstance().sendMainHtmlToPlayer(player);
				}
				GlobalPvPZoneTaskManager.getInstance().setGlobalPvpOn(true);
				GlobalPvPZoneTaskManager.getInstance().startThread();
				break;
			}
			case admin_stop_global_pvp_event:
			{
				for (Zone zone : ReflectionManager.DEFAULT.getZones())
				{
					if (zone.getType() == Zone.ZoneType.global_pvp_zone)
					{
						zone.setActive(false);
					}
				}
				GlobalPvPZoneTaskManager.getInstance().setGlobalPvpOn(false);
				GlobalPvPZoneTaskManager.getInstance().stopThread();
				break;
			}
			default:
			{
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
