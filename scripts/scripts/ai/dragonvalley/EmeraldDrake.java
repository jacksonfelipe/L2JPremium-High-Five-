package ai.dragonvalley;

import premium.commons.util.Rnd;
import premium.gameserver.Config;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Mystic;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.NpcUtils;

/**
 * @author FandC Emerald Drake(22829) Spawns leeches (22860) when attacked.
 */
public class EmeraldDrake extends Mystic
{
	
	public EmeraldDrake(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if (Rnd.chance(Config.EDRAKE_MS_CHANCE))
		{
			NpcInstance actor = getActor();
			NpcInstance n = NpcUtils.spawnSingle(22860, (actor.getX() + Rnd.get(-100, 100)), (actor.getY() + Rnd.get(-100, 100)), actor.getZ());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
		}
		super.onEvtAttacked(attacker, damage);
	}
}
