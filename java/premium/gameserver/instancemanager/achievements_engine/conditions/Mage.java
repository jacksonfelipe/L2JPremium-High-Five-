package premium.gameserver.instancemanager.achievements_engine.conditions;

import premium.gameserver.instancemanager.achievements_engine.base.Condition;
import premium.gameserver.model.Player;

public class Mage extends Condition
{
	public Mage(Object value)
	{
		super(value);
		setName("Be Mage");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		
		if (player.isMageClass())
		{
			return true;
		}
		
		return false;
	}
}