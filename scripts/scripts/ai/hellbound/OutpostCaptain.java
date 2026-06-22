package ai.hellbound;

import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.World;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.scripts.Functions;

public class OutpostCaptain extends Fighter
{
	private boolean _attacked = false;
	
	public OutpostCaptain(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if (attacker == null || attacker.getPlayer() == null)
		{
			return;
		}
		
		for (NpcInstance minion : World.getAroundNpc(getActor(), 3000, 2000))
		{
			if (minion.getNpcId() == 22358 || minion.getNpcId() == 22357)
			{
				minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);
			}
		}
		
		if (!_attacked)
		{
			Functions.npcSay(getActor(), "Fool, you and your friends will die! Attack!");
			_attacked = true;
		}
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
	
}