package premium.gameserver.instancemanager.achievements_engine.conditions;

import premium.gameserver.instancemanager.achievements_engine.base.Condition;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.Hero;
import premium.gameserver.model.entity.olympiad.Olympiad;
import premium.gameserver.templates.StatsSet;

public class HeroCount extends Condition
{
	public HeroCount(Object value)
	{
		super(value);
		setName("Hero Count");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		
		int val = Integer.parseInt(getValue().toString());
		for (int hero : Hero.getInstance().getHeroes().keySet())
		{
			if (hero == player.getObjectId())
			{
				StatsSet sts = Hero.getInstance().getHeroes().get(hero);
				if (sts.getString(Olympiad.CHAR_NAME).equals(player.getName()))
				{
					if (sts.getInteger(Hero.COUNT) >= val)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}