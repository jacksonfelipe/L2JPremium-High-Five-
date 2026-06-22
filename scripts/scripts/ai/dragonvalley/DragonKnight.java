package ai.dragonvalley;

import premium.commons.util.Rnd;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.NpcUtils;

public class DragonKnight extends Fighter
{
	public DragonKnight(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		switch (getActor().getNpcId())
		{
			case 22844:
				if (Rnd.chance(50))
				{
					NpcInstance n = NpcUtils.spawnSingle(22845, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
			case 22845:
				if (Rnd.chance(50))
				{
					NpcInstance n = NpcUtils.spawnSingle(22846, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
		}
		
	}
}