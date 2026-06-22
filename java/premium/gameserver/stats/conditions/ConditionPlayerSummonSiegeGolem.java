package premium.gameserver.stats.conditions;

import premium.gameserver.model.Player;
import premium.gameserver.model.Zone;
import premium.gameserver.model.entity.events.impl.CastleSiegeEvent;
import premium.gameserver.model.entity.events.impl.SiegeEvent;
import premium.gameserver.stats.Env;

public class ConditionPlayerSummonSiegeGolem extends Condition
{
	public ConditionPlayerSummonSiegeGolem()
	{
		//
	}
	
	@Override
	protected boolean testImpl(Env env)
	{
		Player player = env.character.getPlayer();
		if (player == null)
		{
			return false;
		}
		Zone zone = player.getZone(Zone.ZoneType.RESIDENCE);
		if (zone != null)
		{
			return false;
		}
		zone = player.getZone(Zone.ZoneType.SIEGE);
		if (zone == null)
		{
			return false;
		}
		SiegeEvent<?, ?> event = player.getEvent(SiegeEvent.class);
		if (event == null)
		{
			return false;
		}
		if (event instanceof CastleSiegeEvent)
		{
			if ((zone.getParams().getInteger("residence") != event.getId()) || (event.getSiegeClan(CastleSiegeEvent.ATTACKERS, player.getClan()) == null))
			{
				return false;
			}
		}
		else if (event.getSiegeClan(CastleSiegeEvent.DEFENDERS, player.getClan()) == null)
		{
			return false;
		}
		
		return true;
	}
}
