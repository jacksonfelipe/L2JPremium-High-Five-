package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.ExBR_ProductInfo;

public class RequestExBR_ProductInfo extends L2GameClientPacket
{
	private int _productId;
	
	@Override
	protected void readImpl()
	{
		this._productId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(new ExBR_ProductInfo(this._productId));
	}
}