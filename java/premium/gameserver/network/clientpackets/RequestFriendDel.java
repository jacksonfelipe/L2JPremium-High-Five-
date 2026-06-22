package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;

public class RequestFriendDel extends L2GameClientPacket
{
	private String _name;
	
	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		player.getFriendList().removeFriend(this._name);
	}
}