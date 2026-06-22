package ai.hellbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Fighter;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.SimpleSpawner;
import premium.gameserver.model.instances.DoorInstance;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.Location;
import premium.gameserver.utils.ReflectionUtils;

public class Darion extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(Darion.class);
	
	private static final int[] doors =
	{
		20250009,
		20250004,
		20250005,
		20250006,
		20250007
	};
	
	public Darion(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		
		NpcInstance actor = getActor();
		for (int i = 0; i < 5; i++)
		{
			try
			{
				SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(Rnd.get(25614, 25615)));
				sp.setLoc(Location.findPointToStay(actor, 400, 900));
				sp.doSpawn(true);
				sp.stopRespawn();
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on Darion Spawn", e);
			}
		}
		
		// Doors
		for (int door2 : doors)
		{
			DoorInstance door = ReflectionUtils.getDoor(door2);
			door.closeMe();
		}
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		// Doors
		for (int door2 : doors)
		{
			DoorInstance door = ReflectionUtils.getDoor(door2);
			door.openMe();
		}
		
		for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(25614, false))
		{
			npc.deleteMe();
		}
		
		for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(25615, false))
		{
			npc.deleteMe();
		}
		
		super.onEvtDead(killer);
	}
	
}