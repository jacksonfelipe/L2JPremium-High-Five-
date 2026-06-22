package premium.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.model.Player;
import premium.gameserver.model.base.Element;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.templates.item.ItemTemplate;

/**
 * @author SYS
 */
public class ExShowBaseAttributeCancelWindow extends L2GameServerPacket
{
	private final List<ItemInstance> _items = new ArrayList<ItemInstance>();
	
	public ExShowBaseAttributeCancelWindow(Player activeChar)
	{
		for (ItemInstance item : activeChar.getInventory().getItems())
		{
			if (item.getAttributeElement() == Element.NONE || !item.canBeEnchanted(true) || getAttributeRemovePrice(item) == 0)
			{
				continue;
			}
			this._items.add(item);
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x74);
		this.writeD(this._items.size());
		for (ItemInstance item : this._items)
		{
			this.writeD(item.getObjectId());
			this.writeQ(getAttributeRemovePrice(item));
		}
	}
	
	public static long getAttributeRemovePrice(ItemInstance item)
	{
		switch (item.getCrystalType())
		{
			case S:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 50000 : 40000;
			case S80:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 100000 : 80000;
			case S84:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 200000 : 160000;
			default:
				break;
		}
		return 0;
	}
}