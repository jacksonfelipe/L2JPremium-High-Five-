package ai.hellbound;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.DefaultAI;
import premium.gameserver.instancemanager.naia.NaiaCoreManager;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.ReflectionUtils;

public class NaiaCube extends DefaultAI
{
	
	public NaiaCube(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}
	
	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		ThreadPoolManager.getInstance().schedule(new Despawn(getActor()), 300 * 1000L);
	}
	
	private class Despawn extends RunnableImpl
	{
		NpcInstance _npc;
		
		private Despawn(NpcInstance npc)
		{
			_npc = npc;
		}
		
		@Override
		public void runImpl()
		{
			_npc.deleteMe();
			NaiaCoreManager.setZoneActive(false);
			ReflectionUtils.getDoor(20240001).openMe(); // Beleth Door
			ReflectionUtils.getDoor(18250025).openMe(); // Epidos Door
		}
	}
}