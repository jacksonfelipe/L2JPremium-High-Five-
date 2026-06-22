package premium.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.Config;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.templates.StatsSet;

public class SelfSacrifice extends Skill
{
	private final int _effRadius;
	
	public SelfSacrifice(StatsSet set)
	{
		super(set);
		_effRadius = set.getInteger("effRadius", 1000);
		_lethal1 = set.getInteger("lethal1", 0);
		_lethal2 = set.getInteger("lethal2", 0);
	}
	
	@Override
	public List<Creature> getTargets(Creature activeChar, Creature aimingTarget, boolean forceUse)
	{
		List<Creature> result = new ArrayList<>();
		if (((activeChar.getAroundCharacters(_effRadius, 1000) == null) || (activeChar.getAroundCharacters(_effRadius, 1000).isEmpty())) && (((Player) activeChar).getParty() == null))
		{
			return result;
		}
		for (int i = 0; i < activeChar.getAroundCharacters(_effRadius, 1000).size(); i++)
		{
			Creature target = activeChar.getAroundCharacters(_effRadius, 1000).get(i);
			if ((target != null) && (target.isPlayer()) && (!target.isAutoAttackable(activeChar)))
			{
				if (target.isPlayer())
				{
					Player activeCharTarget = (Player) target;
					if ((activeCharTarget.isInDuel()) || (activeCharTarget.isCursedWeaponEquipped()))
					{
						
					}
				}
				else
				{
					result.add(target);
				}
			}
		}
		return result;
	}
	
	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{
				if ((getSkillType() != Skill.SkillType.BUFF) || (target == activeChar) || ((!target.isCursedWeaponEquipped()) && (!activeChar.isCursedWeaponEquipped())))
				{
					boolean reflected = target.checkReflectSkill(activeChar, this);
					getEffects(activeChar, target, getActivateRate() > 0, false, reflected);
				}
			}
		}
		if ((isSSPossible()) && ((!Config.SAVING_SPS) || (_skillType != Skill.SkillType.SELF_SACRIFICE)))
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}