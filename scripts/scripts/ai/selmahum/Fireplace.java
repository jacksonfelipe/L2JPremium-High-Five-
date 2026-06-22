package ai.selmahum;

import premium.commons.threading.RunnableImpl;
import premium.commons.util.Rnd;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.DefaultAI;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.NpcUtils;

public class Fireplace extends DefaultAI
{
	private static final long delay = 5 * 60 * 1000L;
	
	public Fireplace(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}
	
	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if (Rnd.chance(60))
		{
			getActor().setNpcState(1);
		}
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Switch(), 10000L, delay);
	}
	
	public class Switch extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (actor.getNpcState() == 1)
			{
				actor.setNpcState(0);
			}
			else
			{
				actor.setNpcState(1);
				if (Rnd.chance(70))
				{
					NpcUtils.spawnSingle(18933, actor.getLoc(), delay / 2);
				}
			}
		}
	}
	
	public class DeleteCauldron extends RunnableImpl
	{
		NpcInstance _npc;
		
		public DeleteCauldron(NpcInstance npc)
		{
			_npc = npc;
		}
		
		@Override
		public void runImpl()
		{
			_npc.deleteMe();
		}
	}
	
	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}