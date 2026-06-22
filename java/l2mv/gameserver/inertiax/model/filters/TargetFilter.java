package l2mv.gameserver.inertiax.model.filters;

import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.inertiax.enums.EPanelOption;
import l2mv.gameserver.inertiax.enums.ETargetType;
import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.panels.TargetFiltering;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.RaidBossInstance;
import l2mv.gameserver.utils.Location;

public class TargetFilter extends InertiaFilter<Creature>
{
	public TargetFilter(final Inertia inertia)
	{
		super(inertia);
	}

	@Override
	public boolean test(Creature target)
	{
		final Player activeChar = _inertia.getActivePlayer();
		if (activeChar == null)
			return false;
		
		if (target.isAlikeDead())
			return false;
		
		final ETargetType targetType = _inertia.getTargetType();
		if (targetType == ETargetType.MONSTRO)
		{
			if (target.isRaid() || target.isBoss())
				return false;
			if (target.getTarget() != activeChar && target.getMaxHp() > 300_000)
				return false;
		}
		else if (targetType == ETargetType.RAID_BOSS)
		{
			if (!target.isRaid() || target.isBoss())
				return false;
		}
		else if (targetType == ETargetType.GRAND_BOSS)
		{
			if (!target.isBoss())
				return false;
		}
		
		if (!target.isAutoAttackable(activeChar))
			return false;
		
		if (!GeoEngine.canSeeTarget(activeChar, target, false))
			return false;
		
		final var panel = _inertia.getPanel(EPanelOption.Target_Filter);
		if (panel instanceof TargetFiltering && target.isNpc())
		{
			TargetFiltering targetFiltering = (TargetFiltering) panel;
			if (targetFiltering.isFilteredId(target.getNpcId()))
				return false;
		}
		
		final Location loc = _inertia.getSearchLocation();
		final var searchType = _inertia.getSearchType();
		if (loc != null && !target.isInRange(loc, _inertia.getSearchRange()))
			return false;
		
		return true;
	}
}

