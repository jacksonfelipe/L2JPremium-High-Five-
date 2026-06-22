package npc.model.residences.castle;

import java.util.HashSet;
import java.util.Set;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Spawner;
import premium.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;

public class CastleControlTowerInstance extends SiegeToggleNpcInstance
{
	private Set<Spawner> _spawnList = new HashSet<Spawner>();
	
	public CastleControlTowerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onDeathImpl(Creature killer)
	{
		for (Spawner spawn : _spawnList)
		{
			spawn.stopRespawn();
		}
		_spawnList.clear();
	}
	
	@Override
	public void register(Spawner spawn)
	{
		_spawnList.add(spawn);
	}
}