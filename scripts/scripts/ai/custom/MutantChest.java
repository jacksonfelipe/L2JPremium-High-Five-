package ai.custom;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.scripts.Functions;

public class MutantChest extends Fighter
{
	public MutantChest(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if (Rnd.chance(30))
		{
			Functions.npcSay(actor, "Enemies! Enemies everywhere! Everything here, the enemies here!");
		}
		
		actor.deleteMe();
	}
}