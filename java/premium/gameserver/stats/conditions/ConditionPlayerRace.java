package premium.gameserver.stats.conditions;

import premium.gameserver.model.Player;
import premium.gameserver.model.base.Race;
import premium.gameserver.stats.Env;

public class ConditionPlayerRace extends Condition
{
	private final Race _race;
	
	public ConditionPlayerRace(String race)
	{
		_race = Race.valueOf(race.toLowerCase());
	}
	
	@Override
	protected boolean testImpl(Env env)
	{
		if (!env.character.isPlayer())
		{
			return false;
		}
		return ((Player) env.character).getRace() == _race;
	}
}