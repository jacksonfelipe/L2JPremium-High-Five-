package l2mv.gameserver.inertiax.model.filters;

import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.InertiaCast;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;

public class ActionFilter extends InertiaFilter<InertiaCast> 
{
	public ActionFilter(final Inertia inertia)
	{
		super(inertia);
	}

	@Override
	public boolean test(final InertiaCast inertiaCast)
	{
		if (inertiaCast == null)
			return false;
		if (inertiaCast.isReuse())
			return false;
		final var player = _inertia.getActivePlayer();
		if (player == null)
			return false;
		if (!inertiaCast.isUserHp(player))
			return false;
		final GameObject targetObj = player.getTarget();
		if (targetObj != null && targetObj.isCreature() && !inertiaCast.isTargetHp((Creature) targetObj))
			return false;
		return true;
	}
	
}
