package premium.gameserver.listener.zone.impl;

import premium.gameserver.listener.zone.OnZoneEnterLeaveListener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Zone;
import premium.gameserver.model.entity.residence.Residence;
import premium.gameserver.model.entity.residence.ResidenceFunction;
import premium.gameserver.stats.Stats;
import premium.gameserver.stats.funcs.FuncMul;

public class ResidenceEnterLeaveListenerImpl implements OnZoneEnterLeaveListener
{
	public static final OnZoneEnterLeaveListener STATIC = new ResidenceEnterLeaveListenerImpl();
	
	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if (!actor.isPlayer())
		{
			return;
		}
		
		Player player = (Player) actor;
		Residence residence = (Residence) zone.getParams().get("residence");
		
		if (residence.getOwner() == null || residence.getOwner() != player.getClan())
		{
			return;
		}
		
		if (residence.isFunctionActive(ResidenceFunction.RESTORE_HP))
		{
			double value = 1. + residence.getFunction(ResidenceFunction.RESTORE_HP).getLevel() / 100.;
			
			player.addStatFunc(new FuncMul(Stats.REGENERATE_HP_RATE, 0x30, residence, value));
		}
		
		if (residence.isFunctionActive(ResidenceFunction.RESTORE_MP))
		{
			double value = 1. + residence.getFunction(ResidenceFunction.RESTORE_MP).getLevel() / 100.;
			
			player.addStatFunc(new FuncMul(Stats.REGENERATE_MP_RATE, 0x30, residence, value));
		}
	}
	
	@Override
	public void onZoneLeave(Zone zone, Creature actor)
	{
		if (!actor.isPlayer())
		{
			return;
		}
		
		Residence residence = (Residence) zone.getParams().get("residence");
		
		actor.removeStatsOwner(residence);
	}
	
	@Override
	public void onEquipChanged(Zone zone, Creature actor)
	{
	}
}
