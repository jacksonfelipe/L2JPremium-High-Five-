package l2mv.gameserver.inertiax.model.filters;

import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.model.Creature;

public class VTargetFilter extends InertiaFilter<Creature>
{
	public VTargetFilter(final Inertia inertia)
	{
		super(inertia);
	}

	@Override
	public boolean test(Creature target)
	{
		if (target.isAlikeDead())
			return false;
		
		final var activeChar = _inertia.getActivePlayer();
		if (activeChar == null)
			return false;
		
		if (!GeoEngine.canSeeTarget(activeChar, target, false))
			return false;
		
		if (!target.isAutoAttackable(activeChar))
			return false;
		return true;
	}
}
