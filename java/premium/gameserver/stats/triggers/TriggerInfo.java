package premium.gameserver.stats.triggers;

import premium.commons.lang.ArrayUtils;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.stats.Env;
import premium.gameserver.stats.conditions.Condition;

public class TriggerInfo extends Skill.AddedSkill
{
	private final TriggerType _type;
	private final double _chance;
	private Condition[] _conditions = Condition.EMPTY_ARRAY;
	
	public TriggerInfo(int id, int level, TriggerType type, double chance)
	{
		super(id, level);
		_type = type;
		_chance = chance;
	}
	
	public final void addCondition(Condition c)
	{
		_conditions = ArrayUtils.add(_conditions, c);
	}
	
	public boolean checkCondition(Creature actor, Creature target, Creature aimTarget, Skill owner, double damage)
	{
		// Скилл проверяется и кастуется на aimTarget
		if (getSkill().checkTarget(actor, aimTarget, aimTarget, false, false) != null)
		{
			return false;
		}
		
		Env env = new Env();
		env.character = actor;
		env.skill = owner;
		if (owner != null && owner.getId() == Skill.SKILL_SERVITOR_SHARE)
		{
			env.target = actor.getPlayer().getPet();
		}
		else
		{
			env.target = target;
		}
		env.value = damage;
		
		for (Condition c : _conditions)
		{
			if (!c.test(env))
			{
				return false;
			}
		}
		return true;
	}
	
	public TriggerType getType()
	{
		return _type;
	}
	
	public double getChance()
	{
		return _chance;
	}
}
