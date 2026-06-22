package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.entity.olympiad.Olympiad;
import premium.gameserver.network.serverpackets.ExReceiveOlympiad;
import premium.gameserver.network.serverpackets.components.SystemMsg;

public class RequestOlympiadMatchList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd())
		{
			player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return;
		}
		
		if (player.isInOlympiadMode())
		{
			return;
		}
		
		player.sendPacket(new ExReceiveOlympiad.MatchList());
	}
}
