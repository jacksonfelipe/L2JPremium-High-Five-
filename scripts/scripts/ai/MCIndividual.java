package ai;

import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.DefaultAI;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.SocialAction;

/**
 * @author claww - AI for individual monsters (32439, 32440, 32441). - Indicates social programs. - AI is tested and works.
 */
public class MCIndividual extends DefaultAI
{
	public MCIndividual(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}
		
		ThreadPoolManager.getInstance().schedule(new ScheduleSocial(), 1000);
		super.onEvtSpawn();
	}
	
	private class ScheduleSocial implements Runnable
	{
		@Override
		public void run()
		{
			NpcInstance actor = getActor();
			actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
		}
	}
}