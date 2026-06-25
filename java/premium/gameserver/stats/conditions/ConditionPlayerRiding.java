package premium.gameserver.stats.conditions;

import premium.gameserver.model.Player;
import premium.gameserver.stats.Env;

public class ConditionPlayerRiding extends Condition
{
	public enum CheckPlayerRiding
	{
		NONE,
		STRIDER,
		WYVERN
	}
	
	private final CheckPlayerRiding _riding;
	
	public ConditionPlayerRiding(CheckPlayerRiding riding)
	{
		_riding = riding;
	}
	
	@Override
	protected boolean testImpl(Env env)
	{
		if (!env.character.isPlayer())
		{
			return false;
		}
		if ((_riding == CheckPlayerRiding.STRIDER && ((Player) env.character).isRiding()) || (_riding == CheckPlayerRiding.WYVERN && env.character.isFlying()))
		{
			return true;
		}
		if (_riding == CheckPlayerRiding.NONE && !((Player) env.character).isRiding() && !env.character.isFlying())
		{
			return true;
		}
		return false;
	}
}
