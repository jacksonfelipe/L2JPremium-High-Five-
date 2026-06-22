package l2mv.gameserver.inertiax.model.filters;

import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.InertiaCast;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;

public class AvailSkillActionFilter extends InertiaFilter<InertiaCast> 
{
	public AvailSkillActionFilter(final Inertia inertia)
	{
		super(inertia);
	}

	@Override
	public boolean test(final InertiaCast inertiaCast)
	{
		if (inertiaCast == null)
			return false;
		
		final Player player = _inertia.getActivePlayer();
		
		if (player == null)
			return false;
		
		final var skill = _inertia.getSkill(inertiaCast);
		
		if (skill == null)
			return false;
		
		if (player.isSkillDisabled(skill))
			return false;
		
		final GameObject target = player.getTarget();
		if (target != null && target.isCreature())
		{
			if (!skill.checkCondition(player, (Creature) target, false, false, true))
				return false;
		}
		else
		{
			if (!skill.checkCondition(player, null, false, false, true))
				return false;
		}
		
		return true;
	}
	
}
