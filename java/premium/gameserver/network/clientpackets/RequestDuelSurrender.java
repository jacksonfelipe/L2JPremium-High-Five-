package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.entity.events.impl.DuelEvent;

public class RequestDuelSurrender extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		DuelEvent duelEvent = player.getEvent(DuelEvent.class);
		if (duelEvent == null)
		{
			return;
		}
		
		duelEvent.packetSurrender(player);
	}
}