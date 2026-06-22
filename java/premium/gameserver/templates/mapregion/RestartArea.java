package premium.gameserver.templates.mapregion;

import java.util.Map;

import premium.gameserver.model.Territory;
import premium.gameserver.model.base.Race;

public class RestartArea implements RegionData
{
	private final Territory _territory;
	private final Map<Race, RestartPoint> _restarts;
	
	public RestartArea(Territory territory, Map<Race, RestartPoint> restarts)
	{
		_territory = territory;
		_restarts = restarts;
	}
	
	@Override
	public Territory getTerritory()
	{
		return _territory;
	}
	
	public Map<Race, RestartPoint> getRestartPoint()
	{
		return _restarts;
	}
}
