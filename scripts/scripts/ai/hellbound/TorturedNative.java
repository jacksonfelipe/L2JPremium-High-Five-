package ai.hellbound;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.scripts.Functions;

public class TorturedNative extends Fighter
{
	public TorturedNative(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return true;
		}
		
		if (Rnd.chance(1))
		{
			if (Rnd.chance(10))
			{
				Functions.npcSay(actor, "Eeeek... I feel sick... yow...!");
			}
			else
			{
				Functions.npcSay(actor, "It... will... kill... everyone...!");
			}
		}
		
		return super.thinkActive();
	}
}