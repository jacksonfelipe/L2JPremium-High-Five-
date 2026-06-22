package ai;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Zone;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.ReflectionUtils;

public class Anais extends Fighter
{
	private static Zone _zone;
	
	public Anais(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
		_zone = ReflectionUtils.getZone("[FourSepulchers1]");
	}
	
	/*
	 * @Override protected boolean maybeMoveToHome() { NpcInstance actor = getActor(); if (actor != null && !_zone.checkIfInZone(actor)) teleportHome(true); return false; }
	 */
	
	public static Zone getZone()
	{
		return _zone;
	}
	
	@Override
	public boolean canSeeInSilentMove(Playable target)
	{
		return (!target.isSilentMoving()) || (Rnd.chance(10));
	}
}