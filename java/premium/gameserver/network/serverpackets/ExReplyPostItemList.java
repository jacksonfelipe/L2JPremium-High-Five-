package premium.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInfo;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.clientpackets.RequestExPostItemList;

/**
 * Ответ на запрос создания нового письма. Отсылается при получении {@link RequestExPostItemList} Содержит список вещей, которые можно приложить к письму.
 */
public class ExReplyPostItemList extends L2GameServerPacket
{
	private List<ItemInfo> _itemsList = new ArrayList<>();
	
	public ExReplyPostItemList(Player activeChar)
	{
		ItemInstance[] items = activeChar.getInventory().getItems();
		for (ItemInstance item : items)
		{
			if (item.canBeTraded(activeChar))
			{
				this._itemsList.add(new ItemInfo(item));
			}
		}
	}
	
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xB2);
		this.writeD(this._itemsList.size());
		for (ItemInfo item : this._itemsList)
		{
			this.writeItemInfo(item);
		}
	}
}