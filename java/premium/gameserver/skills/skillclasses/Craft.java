package premium.gameserver.skills.skillclasses;

import java.util.List;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.network.serverpackets.RecipeBookItemList;
import premium.gameserver.templates.StatsSet;

public class Craft extends Skill
{
	private final boolean _dwarven;
	
	public Craft(StatsSet set)
	{
		super(set);
		_dwarven = set.getBool("isDwarven");
	}
	
	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Player p = (Player) activeChar;
		if (p.isInStoreMode() || p.isProcessingRequest())
		{
			return false;
		}
		
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}
	
	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		activeChar.sendPacket(new RecipeBookItemList((Player) activeChar, _dwarven));
	}
}
