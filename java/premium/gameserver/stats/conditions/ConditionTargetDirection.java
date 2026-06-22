package premium.gameserver.stats.conditions;

import premium.gameserver.stats.Env;
import premium.gameserver.utils.PositionUtils;

public class ConditionTargetDirection extends Condition
{
	private final PositionUtils.TargetDirection _dir;
	
	public ConditionTargetDirection(PositionUtils.TargetDirection direction)
	{
		_dir = direction;
	}
	
	@Override
	protected boolean testImpl(Env env)
	{
		return PositionUtils.getDirectionTo(env.target, env.character) == _dir;
	}
}
