package premium.gameserver.stats;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.model.items.ItemInstance;

public final class Env
{
	public Creature character;
	public Creature target;
	public ItemInstance item;
	public Skill skill;
	public double value;
	
	public Env()
	{
	}
	
	public Env(Creature cha, Creature tar, Skill sk)
	{
		character = cha;
		target = tar;
		skill = sk;
	}
}
