package zones;

import premium.gameserver.listener.zone.OnZoneEnterLeaveListener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Zone;
import premium.gameserver.network.serverpackets.components.CustomMessage;
import premium.gameserver.scripts.ScriptFile;
import premium.gameserver.utils.Location;
import premium.gameserver.utils.ReflectionUtils;

/**
 * Created by dkn on 18-Oct-16.
 */
public class BelethWater implements ScriptFile
{
	
	private static ZoneListener _zoneListener;
	
	@Override
	public void onLoad()
	{
		_zoneListener = new ZoneListener();
		Zone zone = ReflectionUtils.getZone("[beleth_water]");
		zone.addListener(_zoneListener);
	}
	
	@Override
	public void onReload()
	{
		
	}
	
	@Override
	public void onShutdown()
	{
		
	}
	
	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if (zone.getParams() == null || !cha.isPlayable() || cha.getPlayer().isGM())
			{
				return;
			}
			if (cha.getLevel() > zone.getParams().getInteger("levelLimit"))
			{
				if (cha.isPlayer())
				{
					cha.getPlayer().sendMessage(new CustomMessage("scripts.zones.epic.banishMsg", (Player) cha));
				}
				cha.teleToLocation(Location.parseLoc(zone.getParams().getString("tele")));
			}
		}
		
		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			
		}
		
		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}
}
