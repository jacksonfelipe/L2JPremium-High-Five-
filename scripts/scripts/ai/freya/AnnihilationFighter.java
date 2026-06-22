package ai.freya;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Playable;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.Location;
import premium.gameserver.utils.NpcUtils;

public class AnnihilationFighter extends Fighter
{
	public AnnihilationFighter(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(5))
		{
			NpcUtils.spawnSingle(18839, Location.findPointToStay(getActor(), 40, 120), getActor().getReflection()); // Maguen
		}
		
		super.onEvtDead(killer);
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
}