package premium.gameserver.stats.conditions;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.base.Race;
import premium.gameserver.stats.Env;

public class ConditionTargetPlayerRace extends Condition
{
	private final Race _race;
	
	public ConditionTargetPlayerRace(String race)
	{
		_race = Race.valueOf(race.toLowerCase());
	}
	
	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		return target != null && target.isPlayer() && _race == ((Player) target).getRace();
	}
}