package ai.primeval_isle;

import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Skill;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.SocialAction;
import premium.gameserver.tables.SkillTable;

public class SprigantStun extends Fighter
{
	
	private final Skill SKILL = SkillTable.getInstance().getInfo(5085, 1);
	private long _waitTime;
	private static final int TICK_IN_MILISECONDS = 15000;
	
	public SprigantStun(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (System.currentTimeMillis() > _waitTime)
		{
			actor.doCast(SKILL, actor, false);
			_waitTime = System.currentTimeMillis() + TICK_IN_MILISECONDS;
		}
		actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
		super.thinkActive();
		return true;
	}
}
