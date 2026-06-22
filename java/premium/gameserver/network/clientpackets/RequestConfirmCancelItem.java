package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.ExPutItemResultForVariationCancel;
import premium.gameserver.network.serverpackets.components.SystemMsg;

public class RequestConfirmCancelItem extends L2GameClientPacket
{
	// format: (ch)d
	int _itemId;
	
	@Override
	protected void readImpl()
	{
		this._itemId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		
		// if (!activeChar.checkLastAugmentNpc())
		// {
		// return;
		// }
		//
		ItemInstance item = activeChar.getInventory().getItemByObjectId(this._itemId);
		
		if (item == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (!item.isAugmented())
		{
			activeChar.sendPacket(SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}
		
		activeChar.sendPacket(new ExPutItemResultForVariationCancel(item));
	}
}