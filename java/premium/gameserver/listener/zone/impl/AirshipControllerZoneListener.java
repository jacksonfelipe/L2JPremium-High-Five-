package premium.gameserver.listener.zone.impl;

import premium.gameserver.listener.zone.OnZoneEnterLeaveListener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Zone;
import premium.gameserver.model.entity.boat.ClanAirShip;
import premium.gameserver.model.instances.ClanAirShipControllerInstance;

public class AirshipControllerZoneListener implements OnZoneEnterLeaveListener
{
	private ClanAirShipControllerInstance _controllerInstance;
	
	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if (_controllerInstance == null && actor instanceof ClanAirShipControllerInstance)
		{
			_controllerInstance = (ClanAirShipControllerInstance) actor;
		}
		else if (actor.isClanAirShip())
		{
			_controllerInstance.setDockedShip((ClanAirShip) actor);
		}
	}
	
	@Override
	public void onZoneLeave(Zone zone, Creature actor)
	{
		if (actor.isClanAirShip())
		{
			_controllerInstance.setDockedShip(null);
		}
	}
	
	@Override
	public void onEquipChanged(Zone zone, Creature actor)
	{
	}
}
