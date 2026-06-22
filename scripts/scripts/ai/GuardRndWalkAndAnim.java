package ai;

import premium.gameserver.ai.Guard;
import premium.gameserver.model.instances.NpcInstance;

public class GuardRndWalkAndAnim extends Guard
{
	public GuardRndWalkAndAnim(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected boolean thinkActive()
	{
		if (super.thinkActive() || randomAnimation() || randomWalk())
		{
			return true;
		}
		
		return false;
	}
}