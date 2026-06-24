package premium.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import premium.gameserver.model.items.TradeItem;

/**
 * Format: c ddh[hdddhhd] c - id (0xE8) d - money d - manor id h - size [ h - item type 1 d - object id d - item id d - count h - item type 2 h d - price ]
 */
public final class BuyListSeed extends L2GameServerPacket
{
	private int _manorId;
	private List<TradeItem> _list = new ArrayList<>();
	private long _money;
	
	public BuyListSeed(NpcTradeList list, int manorId, long currentMoney)
	{
		this._money = currentMoney;
		this._manorId = manorId;
		this._list = list.getItems();
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeC(0xe9);
		
		this.writeQ(this._money); // current money
		this.writeD(this._manorId); // manor id
		
		this.writeH(this._list.size()); // list length
		
		for (TradeItem item : this._list)
		{
			this.writeItemInfo(item);
			this.writeQ(item.getOwnersPrice());
		}
	}
}