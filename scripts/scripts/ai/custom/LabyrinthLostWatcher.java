package ai.custom;

import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.model.instances.ReflectionBossInstance;
import premium.gameserver.stats.Stats;
import premium.gameserver.stats.funcs.FuncSet;

public class LabyrinthLostWatcher extends Fighter
{
	
	public LabyrinthLostWatcher(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Reflection r = actor.getReflection();
		if (!r.isDefault())
		{
			if (checkMates(actor.getNpcId()))
			{
				if (findLostCaptain() != null)
				{
					findLostCaptain().addStatFunc(new FuncSet(Stats.POWER_DEFENCE, 0x30, this, findLostCaptain().getTemplate().basePDef * 0.66));
				}
			}
		}
		super.onEvtDead(killer);
	}
	
	private boolean checkMates(int id)
	{
		for (NpcInstance n : getActor().getReflection().getNpcs())
		{
			if (n.getNpcId() == id && !n.isDead())
			{
				return false;
			}
		}
		return true;
	}
	
	private NpcInstance findLostCaptain()
	{
		for (NpcInstance n : getActor().getReflection().getNpcs())
		{
			if (n instanceof ReflectionBossInstance)
			{
				return n;
			}
		}
		return null;
	}
}