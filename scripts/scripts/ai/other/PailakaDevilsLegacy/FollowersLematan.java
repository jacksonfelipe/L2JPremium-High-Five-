package ai.other.PailakaDevilsLegacy;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.World;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.tables.SkillTable;

/**
 * - AI мобов Followers Lematan, миньёны-лекари Боса Lematan в пайлаке 61-67. - Не умеют ходить, лечат Боса.
 */
public class FollowersLematan extends Fighter
{
	private static int LEMATAN = 18633;
	
	public FollowersLematan(NpcInstance actor)
	{
		super(actor);
		startSkillTimer();
	}
	
	private void findBoss()
	{
		NpcInstance minion = getActor();
		if (minion == null)
		{
			return;
		}
		
		for (NpcInstance target : World.getAroundNpc(minion, 1000, 1000))
		{
			if (target.getNpcId() == LEMATAN && target.getCurrentHpPercents() < 65)
			{
				minion.doCast(SkillTable.getInstance().getInfo(5712, 1), target, true);
			}
		}
		return;
	}
	
	public void startSkillTimer()
	{
		if (getActor() != null)
		{
			ScheduleTimerTask(20000);
		}
	}
	
	public void ScheduleTimerTask(long time)
	{
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				findBoss();
				startSkillTimer();
			}
		}, time);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		// stop timers if any
		super.onEvtDead(killer);
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}