package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;

public class RequestExBuySellUIClose extends L2GameClientPacket
{
	@Override
	protected void runImpl()
	{
		// trigger
	}
	
	@Override
	protected void readImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.setBuyListId(0);
		activeChar.sendItemList(true);
	}
}