package premium.gameserver.stats.conditions;

import premium.gameserver.model.Player;
import premium.gameserver.stats.Env;
import premium.gameserver.templates.item.ArmorTemplate.ArmorType;

public class ConditionUsingArmor extends Condition
{
	private final ArmorType _armor;
	
	public ConditionUsingArmor(ArmorType armor)
	{
		_armor = armor;
	}
	
	@Override
	protected boolean testImpl(Env env)
	{
		if (env.character.isPlayer() && ((Player) env.character).isWearingArmor(_armor))
		{
			return true;
		}
		
		return false;
	}
}
