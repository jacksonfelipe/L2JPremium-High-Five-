package ai.dragonvalley;

import premium.commons.util.Rnd;
import premium.gameserver.Config;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.ai.Mystic;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.NpcUtils;

/**
 * @author FandC После каждой атаки имеет шанс призвать одного из двух мобов.
 */
public class Necromancer extends Mystic
{
	
	public Necromancer(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker == null || actor.isDead())
		{
			return;
		}
		
		actor.getAggroList().addDamageHate(attacker, 0, damage);
		
		if (damage > 0 && (attacker.isSummon() || attacker.isPet()))
		{
			actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? damage : 1);
		}
		
		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			if (!actor.isRunning())
			{
				startRunningTask(AI_TASK_ATTACK_DELAY);
			}
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		if (Rnd.chance(Config.NECROMANCER_MS_CHANCE))
		{
			NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
		}
		notifyFriends(attacker, damage);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
	 
		if (Rnd.chance(Config.NECROMANCER_MS_CHANCE * 2))
		{
			NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
		}
		super.onEvtDead(killer);
	}
}
