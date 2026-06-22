package ai.hellbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.Fighter;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.SimpleSpawner;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.Location;

public class DarionChallenger extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(DarionChallenger.class);
	private static final int TeleportCube = 32467;
	
	public DarionChallenger(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		if (checkAllDestroyed())
		{
			try
			{
				SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(TeleportCube));
				sp.setLoc(new Location(-12527, 279714, -11622, 16384));
				sp.doSpawn(true);
				sp.stopRespawn();
				ThreadPoolManager.getInstance().schedule(new Unspawn(), 600 * 1000L); // 10 mins
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on Darino Challanger Spawn", e);
			}
		}
		super.onEvtDead(killer);
	}
	
	private static boolean checkAllDestroyed()
	{
		if (!GameObjectsStorage.getAllByNpcId(25600, true).isEmpty() || !GameObjectsStorage.getAllByNpcId(25601, true).isEmpty() || !GameObjectsStorage.getAllByNpcId(25602, true).isEmpty())
		{
			return false;
		}
		
		return true;
	}
	
	private class Unspawn extends RunnableImpl
	{
		public Unspawn()
		{
		}
		
		@Override
		public void runImpl()
		{
			for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(TeleportCube, true))
			{
				npc.deleteMe();
			}
		}
	}
}