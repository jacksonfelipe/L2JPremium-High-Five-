package ai.seedofinfinity;

import premium.gameserver.ai.DefaultAI;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;

public class SymbolofCohemenes extends DefaultAI
{
	
	public SymbolofCohemenes(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}