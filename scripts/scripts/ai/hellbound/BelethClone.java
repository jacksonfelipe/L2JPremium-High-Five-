package ai.hellbound;

import premium.gameserver.ai.Mystic;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Playable;
import premium.gameserver.model.instances.NpcInstance;

public class BelethClone extends Mystic
{
	public BelethClone(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
	
	@Override
	protected boolean randomAnimation()
	{
		return false;
	}
	
	@Override
	public boolean canSeeInSilentMove(Playable target)
	{
		return true;
	}
	
	@Override
	public boolean canSeeInHide(Playable target)
	{
		return true;
	}
	
	@Override
	public void addTaskAttack(Creature target)
	{
		return;
	}
	
}