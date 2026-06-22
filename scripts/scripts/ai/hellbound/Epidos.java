package ai.hellbound;

import premium.gameserver.ai.Fighter;
import premium.gameserver.instancemanager.naia.NaiaCoreManager;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;

public class Epidos extends Fighter
{
	
	public Epidos(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		NaiaCoreManager.removeSporesAndSpawnCube();
		super.onEvtDead(killer);
	}
}