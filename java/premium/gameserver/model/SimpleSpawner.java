package premium.gameserver.model;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.commons.collections.MultiValueSet;
import premium.commons.util.Rnd;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.templates.spawn.SpawnRange;
import premium.gameserver.utils.Location;

@SuppressWarnings("serial")
public class SimpleSpawner extends Spawner
{
	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(SimpleSpawner.class);
	
	private NpcTemplate _npcTemplate;
	
	private int _locx, _locy, _locz, _heading;
	private Territory _territory;
	
	public SimpleSpawner(NpcTemplate mobTemplate)
	{
		if (mobTemplate == null)
		{
			throw new NullPointerException();
		}
		
		_npcTemplate = mobTemplate;
		_spawned = new ArrayList<>(1);
	}
	
	public SimpleSpawner(int npcId)
	{
		NpcTemplate mobTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (mobTemplate == null)
		{
			throw new NullPointerException("Not find npc: " + npcId);
		}
		
		_npcTemplate = mobTemplate;
		_spawned = new ArrayList<>(1);
	}
	

	public int getAmount()
	{
		return _maximumCount;
	}
	

	public int getSpawnedCount()
	{
		return _currentCount;
	}
	
	
	public int getSheduledCount()
	{
		return _scheduledCount;
	}
	

	public Territory getTerritory()
	{
		return _territory;
	}
	
	
	public Location getLoc()
	{
		return new Location(_locx, _locy, _locz);
	}

	public int getLocx()
	{
		return _locx;
	}
	
 
	public int getLocy()
	{
		return _locy;
	}
	public int getLocz()
	{
		return _locz;
	}
	
	/**
	 * Return the Identifier of the L2NpcInstance manage by this L2Spwan contained in the L2NpcTemplate.<BR>
	 * <BR>
	 */
	@Override
	public int getCurrentNpcId()
	{
		return _npcTemplate.getNpcId();
	}
	
	@Override
	public SpawnRange getCurrentSpawnRange()
	{
		if (_locx == 0 && _locz == 0)
		{
			return _territory;
		}
		return getLoc();
	}
	
	 
	public int getHeading()
	{
		return _heading;
	}
	
	 
	public void restoreAmount()
	{
		_maximumCount = _referenceCount;
	}
	
 
	public void setTerritory(Territory territory)
	{
		_territory = territory;
	}
	
	/**
	 * Set the position(x, y, z, heading) of the spawn point.
	 * @param loc Location
	 */
	public void setLoc(Location loc)
	{
		_locx = loc.x;
		_locy = loc.y;
		_locz = loc.z;
		_heading = loc.h;
	}
	
	 
	public void setLocx(int locx)
	{
		_locx = locx;
	}
	
 
	public void setLocy(int locy)
	{
		_locy = locy;
	}
	
 
	public void setLocz(int locz)
	{
		_locz = locz;
	}
	
 
	public void setHeading(int heading)
	{
		_heading = heading;
	}
	
	@Override
	public void decreaseCount(NpcInstance oldNpc)
	{
		decreaseCount0(_npcTemplate, oldNpc, oldNpc.getDeadTime());
	}
	
	@Override
	public NpcInstance doSpawn(boolean spawn)
	{
		return doSpawn0(_npcTemplate, spawn, StatsSet.EMPTY);
	}
	
	@Override
	protected NpcInstance initNpc(NpcInstance mob, boolean spawn, MultiValueSet<String> set)
	{
		Location newLoc;
		
		if (_territory != null)
		{
			newLoc = _territory.getRandomLoc(_reflection.getGeoIndex());
			newLoc.setH(Rnd.get(0xFFFF));
		}
		else
		{
			newLoc = getLoc();
			
			newLoc.h = getHeading() == -1 ? Rnd.get(0xFFFF) : getHeading();
		}
		
		return initNpc0(mob, newLoc, spawn, set);
	}
	
	@Override
	public void respawnNpc(NpcInstance oldNpc)
	{
		oldNpc.refreshID();
		initNpc(oldNpc, true, StatsSet.EMPTY);
	}
	
	@Override
	public SimpleSpawner clone()
	{
		SimpleSpawner spawnDat = new SimpleSpawner(_npcTemplate);
		spawnDat.setTerritory(_territory);
		spawnDat.setLocx(_locx);
		spawnDat.setLocy(_locy);
		spawnDat.setLocz(_locz);
		spawnDat.setHeading(_heading);
		spawnDat.setAmount(_maximumCount);
		spawnDat.setRespawnDelay(_respawnDelay, _respawnDelayRandom);
		return spawnDat;
	}
}