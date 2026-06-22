package premium.gameserver.network.clientpackets;

import premium.gameserver.instancemanager.MatchingRoomManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.matching.MatchingRoom;

public class RequestPartyMatchDetail extends L2GameClientPacket
{
	private int _roomId;
	private int _locations;
	private int _level;
	
	/**
	 * Format: dddd
	 */
	@Override
	protected void readImpl()
	{
		this._roomId = this.readD(); // room id, если 0 то autojoin
		this._locations = this.readD(); // location
		this._level = this.readD(); // 1 - all, 0 - my level (только при autojoin)
		// readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if ((player == null) || (player.getMatchingRoom() != null))
		{
			return;
		}
		
		if (this._roomId > 0)
		{
			MatchingRoom room = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.PARTY_MATCHING, this._roomId);
			if (room == null)
			{
				return;
			}
			
			room.addMember(player);
		}
		else
		{
			for (MatchingRoom room : MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, this._locations, this._level == 1, player))
			{
				if (room.addMember(player))
				{
					break;
				}
			}
		}
	}
}