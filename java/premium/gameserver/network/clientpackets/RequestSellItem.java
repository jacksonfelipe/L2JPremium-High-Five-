package premium.gameserver.network.clientpackets;

import org.apache.commons.lang3.ArrayUtils;

import premium.commons.math.SafeMath;
import premium.gameserver.Config;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import premium.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.ExBuySellList;
import premium.gameserver.network.serverpackets.components.SystemMsg;

/**
 * packet type id 0x37 format: cddb, b - array if (ddd)
 */
public class RequestSellItem extends L2GameClientPacket
{
	public int _listId;
	private int _count;
	private int[] _items; // object id
	private long[] _itemQ; // count
	
	@Override
	protected void readImpl()
	{
		this._listId = this.readD();
		this._count = this.readD();
		if (this._count * 16 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._items = new int[this._count];
		this._itemQ = new long[this._count];
		
		for (int i = 0; i < this._count; i++)
		{
			this._items[i] = this.readD(); // object id
			this.readD(); // item id
			this._itemQ[i] = this.readQ(); // count
			if (this._itemQ[i] < 1 || ArrayUtils.indexOf(this._items, this._items[i]) < i)
			{
				this._count = 0;
				break;
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this._count == 0)
		{
			return;
		}
		
		if (activeChar.isActionsDisabled() || activeChar.isBlocked())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		if (activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}
		
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		long totalPrice = 0;
		
		activeChar.getInventory().writeLock();
		try
		{
			for (int i = 0; i < this._count; i++)
			{
				int objectId = this._items[i];
				long count = this._itemQ[i];
				
				ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
				if (item == null || item.getCount() < count || !item.canBeSold(activeChar))
				{
					continue;
				}
				
				long price = SafeMath.mulAndCheck(item.getReferencePrice(), count) / 2; // nr.2 is the price you get on sell.
				if (Config.SELL_ALL_ITEMS_FREE)
				{
					price = 1;
				}
				
				ItemInstance refund = activeChar.getInventory().removeItemByObjectId(objectId, count, "Selling Item");
				if (refund == null)
				{
					continue;
				}
				
				totalPrice = SafeMath.addAndCheck(totalPrice, price);
				activeChar.getRefund().addItem(refund, null, null);
				ItemLogHandler.getInstance().addLog(activeChar, item, count, ItemActionType.SOLD_TO_NPC);
			}
			
			if (totalPrice > 0)
			{
				activeChar.addAdena(totalPrice, true, "Selling Item");
			}
			
			if (activeChar.isBBSUse())
			{
				activeChar.setIsBBSUse(false);
			}
		}
		catch (ArithmeticException ae)
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}
		
		activeChar.sendPacket(new ExBuySellList.SellRefundList(activeChar, true));
		activeChar.sendChanges();
	}
}