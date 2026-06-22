package ai.hellbound;

import premium.gameserver.ai.Fighter;
import premium.gameserver.geodata.GeoEngine;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.Location;

public class FoundryWorker extends Fighter
{
	public FoundryWorker(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker != null)
		{
			Location pos = Location.findPointToStay(actor, 150, 250);
			if (GeoEngine.canMoveToCoord(attacker.getX(), attacker.getY(), attacker.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
			{
				actor.setRunning();
				addTaskMove(pos, false);
			}
		}
	}
	
	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		return false;
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}