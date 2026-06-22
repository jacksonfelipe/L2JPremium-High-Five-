package ai;

import premium.commons.util.Rnd;
import premium.gameserver.Config;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.Fighter;
import premium.gameserver.geodata.GeoEngine;
import premium.gameserver.model.Territory;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.MagicSkillUse;
import premium.gameserver.templates.spawn.SpawnRange;
import premium.gameserver.utils.Location;

/**
 * Моб использует телепортацию вместо рандом валка.
 * @author SYS
 */
public class RndTeleportFighter extends Fighter
{
	private long _lastTeleport;
	
	public RndTeleportFighter(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected boolean maybeMoveToHome()
	{
		NpcInstance actor = getActor();
		if (System.currentTimeMillis() - _lastTeleport < 10000)
		{
			return false;
		}
		
		boolean randomWalk = actor.hasRandomWalk();
		Location sloc = actor.getSpawnedLoc();
		// Random walk or not?
		if ((sloc == null) || (randomWalk && (!Config.RND_WALK || Rnd.chance(Config.RND_WALK_RATE))))
		{
			return false;
		}
		
		if (!randomWalk && actor.isInRangeZ(sloc, Config.MAX_DRIFT_RANGE))
		{
			return false;
		}
		
		int x = sloc.x + Rnd.get(-Config.MAX_DRIFT_RANGE, Config.MAX_DRIFT_RANGE);
		int y = sloc.y + Rnd.get(-Config.MAX_DRIFT_RANGE, Config.MAX_DRIFT_RANGE);
		int z = GeoEngine.getHeight(x, y, sloc.z, actor.getGeoIndex());
		
		if (sloc.z - z > 64)
		{
			return false;
		}
		
		SpawnRange spawnRange = actor.getSpawnRange();
		boolean isInside = true;
		if (spawnRange != null && spawnRange instanceof Territory)
		{
			isInside = ((Territory) spawnRange).isInside(x, y);
		}
		
		if (isInside)
		{
			actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 4671, 1, 500, 0));
			ThreadPoolManager.getInstance().schedule(new Teleport(new Location(x, y, z)), 500);
			_lastTeleport = System.currentTimeMillis();
		}
		return isInside;
	}
}