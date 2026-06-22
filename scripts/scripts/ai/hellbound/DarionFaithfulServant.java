package ai.hellbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.commons.threading.RunnableImpl;
import premium.commons.util.Rnd;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.Fighter;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.SimpleSpawner;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.Location;

public class DarionFaithfulServant extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(DarionFaithfulServant.class);
	private static final int MysteriousAgent = 32372;
	
	public DarionFaithfulServant(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(15))
		{
			try
			{
				SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(MysteriousAgent));
				sp.setLoc(new Location(-11984, 278880, -13599, -4472));
				sp.doSpawn(true);
				sp.stopRespawn();
				ThreadPoolManager.getInstance().schedule(new Unspawn(), 600 * 1000L); // 10 mins
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on Darion Faithful Servanth Death", e);
			}
		}
		super.onEvtDead(killer);
	}
	
	private class Unspawn extends RunnableImpl
	{
		public Unspawn()
		{
		}
		
		@Override
		public void runImpl()
		{
			for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(MysteriousAgent, true))
			{
				npc.deleteMe();
			}
		}
	}
	
}