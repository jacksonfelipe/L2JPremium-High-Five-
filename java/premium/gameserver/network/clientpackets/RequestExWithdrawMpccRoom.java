package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class RequestExWithdrawMpccRoom extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		MatchingRoom room = player.getMatchingRoom();
		if (room == null || room.getType() != MatchingRoom.CC_MATCHING || (room.getLeader() == player))
		{
			return;
		}
		
		room.removeMember(player, false);
	}
}