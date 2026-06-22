package premium.gameserver.network.clientpackets;

import premium.gameserver.instancemanager.MatchingRoomManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.matching.MatchingRoom;

public class RequestExJoinMpccRoom extends L2GameClientPacket
{
	private int _roomId;
	
	@Override
	protected void readImpl()
	{
		this._roomId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if ((player == null) || (player.getMatchingRoom() != null))
		{
			return;
		}
		
		MatchingRoom room = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.CC_MATCHING, this._roomId);
		if (room == null)
		{
			return;
		}
		
		room.addMember(player);
	}
}