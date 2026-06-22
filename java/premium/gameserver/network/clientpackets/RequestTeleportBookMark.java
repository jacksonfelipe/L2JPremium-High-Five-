package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;

public class RequestTeleportBookMark extends L2GameClientPacket
{
	private int slot;
	
	@Override
	protected void readImpl()
	{
		this.slot = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar != null)
		{
			activeChar.bookmarks.tryTeleport(this.slot);
		}
	}
}