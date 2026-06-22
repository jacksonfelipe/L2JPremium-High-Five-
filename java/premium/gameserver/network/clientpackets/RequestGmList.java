package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.tables.GmListTable;

public class RequestGmList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar != null)
		{
			GmListTable.sendListToPlayer(activeChar);
		}
	}
}