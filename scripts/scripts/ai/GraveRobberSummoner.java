package ai;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Mystic;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.stats.Env;
import premium.gameserver.stats.Stats;
import premium.gameserver.stats.funcs.Func;
import premium.gameserver.templates.npc.MinionData;

/**
 * При спавне саммонят случайную охрану. Защита прямо пропорциональна количеству охранников.
 */
public class GraveRobberSummoner extends Mystic
{
	private static final int[] Servitors =
	{
		22683,
		22684,
		22685,
		22686
	};
	
	private int _lastMinionCount = 1;
	
	private class FuncMulMinionCount extends Func
	{
		public FuncMulMinionCount(Stats stat, int order, Object owner)
		{
			super(stat, order, owner);
		}
		
		@Override
		public void calc(Env env)
		{
			env.value *= _lastMinionCount;
		}
	}
	
	public GraveRobberSummoner(NpcInstance actor)
	{
		super(actor);
		
		actor.addStatFunc(new FuncMulMinionCount(Stats.MAGIC_DEFENCE, 0x30, actor));
		actor.addStatFunc(new FuncMulMinionCount(Stats.POWER_DEFENCE, 0x30, actor));
	}
	
	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		
		NpcInstance actor = getActor();
		actor.getMinionList().addMinion(new MinionData(Servitors[Rnd.get(Servitors.length)], Rnd.get(2)));
		_lastMinionCount = Math.max(actor.getMinionList().getAliveMinions().size(), 1);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		MonsterInstance actor = (MonsterInstance) getActor();
		if (actor.isDead())
		{
			return;
		}
		_lastMinionCount = Math.max(actor.getMinionList().getAliveMinions().size(), 1);
		super.onEvtAttacked(attacker, damage);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		actor.getMinionList().deleteMinions();
		super.onEvtDead(killer);
	}
}