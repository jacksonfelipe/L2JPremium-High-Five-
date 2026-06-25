package premium.gameserver.templates.augmentation;

import premium.commons.math.random.RndSelector;

public class OptionGroup
{
	private RndSelector<Integer> _options = new RndSelector<>();
	
	public void addOptionWithChance(int option, int chance)
	{
		_options.add(Integer.valueOf(option), chance);
	}
	
	public Integer random()
	{
		return _options.chance(1000000);
	}
}
