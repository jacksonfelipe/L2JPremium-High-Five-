package premium.gameserver.instancemanager.achievements_engine.conditions;

import premium.gameserver.instancemanager.achievements_engine.base.Condition;
import premium.gameserver.model.Player;

public class Pvp extends Condition
{
	public Pvp(Object value)
	{
		super(value);
		setName("PvP Count");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		
		int val = Integer.parseInt(getValue().toString());
		
		if (player.getPvpKills() >= val)
		{
			return true;
		}
		
		return false;
	}
}