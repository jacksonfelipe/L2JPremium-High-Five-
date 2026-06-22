package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.model.items.PcInventory;
import premium.gameserver.network.serverpackets.ExPutEnchantSupportItemResult;
import premium.gameserver.utils.ItemFunctions;

public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket
{
	private int _itemId;
	private int _catalystId;
	
	@Override
	protected void readImpl()
	{
		this._catalystId = this.readD();
		this._itemId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		PcInventory inventory = activeChar.getInventory();
		ItemInstance itemToEnchant = inventory.getItemByObjectId(this._itemId);
		ItemInstance catalyst = inventory.getItemByObjectId(this._catalystId);
		
		if (ItemFunctions.checkCatalyst(itemToEnchant, catalyst))
		{
			activeChar.sendPacket(new ExPutEnchantSupportItemResult(1));
		}
		else
		{
			activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
		}
	}
}