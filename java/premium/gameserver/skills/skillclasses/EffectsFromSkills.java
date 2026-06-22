package premium.gameserver.skills.skillclasses;

import java.util.List;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.templates.StatsSet;

public class EffectsFromSkills extends Skill
{
	public EffectsFromSkills(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{
				for (AddedSkill as : getAddedSkills())
				{
					as.getSkill().getEffects(activeChar, target, false, false);
				}
			}
		}
	}
}