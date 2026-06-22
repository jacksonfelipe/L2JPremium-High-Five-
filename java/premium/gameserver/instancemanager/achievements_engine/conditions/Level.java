package premium.gameserver.instancemanager.achievements_engine.conditions;

import premium.gameserver.instancemanager.achievements_engine.base.Condition;
import premium.gameserver.model.Player;

public class Level extends Condition
{
	public Level(Object value)
	{
		super(value);
		setName("Level");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		int val = Integer.parseInt(getValue().toString());
		
		if (player.getLevel() >= val)
		{
			return true;
		}
		return false;
	}
}