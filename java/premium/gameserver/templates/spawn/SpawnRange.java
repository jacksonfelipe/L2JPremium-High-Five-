package premium.gameserver.templates.spawn;

import premium.gameserver.utils.Location;

public interface SpawnRange
{
	Location getRandomLoc(int geoIndex);
}
