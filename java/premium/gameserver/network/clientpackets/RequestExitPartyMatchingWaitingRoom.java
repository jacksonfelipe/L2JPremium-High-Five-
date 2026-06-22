package premium.gameserver.network.clientpackets;

import premium.gameserver.instancemanager.MatchingRoomManager;
import premium.gameserver.model.Player;

/**
 * Format: (ch)
 */
public class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket
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
		
		MatchingRoomManager.getInstance().removeFromWaitingList(player);
	}
}