package ai.seedofinfinity;

import premium.gameserver.ai.Fighter;
import premium.gameserver.model.instances.NpcInstance;

public class FeralHound extends Fighter
{
	public FeralHound(NpcInstance actor)
	{
		super(actor);
		actor.setIsInvul(true);
	}
	
	@Override
	protected boolean randomAnimation()
	{
		return false;
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}