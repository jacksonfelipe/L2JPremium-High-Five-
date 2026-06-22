package ai.custom;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.instances.NpcInstance;

public class SSQLilithMinion extends Fighter
{
	private final int[] _enemies =
	{
		32719,
		32720,
		32721
	};
	
	public SSQLilithMinion(NpcInstance actor)
	{
		super(actor);
		actor.setHasChatWindow(false);
	}
	
	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		ThreadPoolManager.getInstance().schedule(new Attack(), 3000);
	}
	
	public class Attack extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (getEnemy() != null)
			{
				getActor().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, getEnemy(), 10000000);
			}
		}
	}
	
	private NpcInstance getEnemy()
	{
		List<NpcInstance> around = getActor().getAroundNpc(1000, 300);
		if (around != null && !around.isEmpty())
		{
			for (NpcInstance npc : around)
			{
				if (ArrayUtils.contains(_enemies, npc.getNpcId()))
				{
					return npc;
				}
			}
		}
		return null;
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}