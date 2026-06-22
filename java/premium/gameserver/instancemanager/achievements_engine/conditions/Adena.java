package premium.gameserver.instancemanager.achievements_engine.conditions;

import premium.gameserver.instancemanager.achievements_engine.base.Condition;
import premium.gameserver.model.Player;

public class Adena extends Condition
{
	public Adena(Object value)
	{
		super(value);
		setName("Adena");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		long val = Integer.parseInt(getValue().toString());
		
		if (player.getInventory().getAdena() >= val)
		{
			return true;
		}
		return false;
	}
}