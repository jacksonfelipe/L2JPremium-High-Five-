package ai.hellbound;

import premium.commons.util.Rnd;
import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.scripts.Functions;

public class TownGuard extends Fighter
{
	public TownGuard(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
		{
			Functions.npcSay(actor, "Invader!");
		}
		super.onIntentionAttack(target);
	}
}