package premium.gameserver.instancemanager.itemauction;

import premium.gameserver.model.items.ItemInfo;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.ItemFunctions;

/**
 * @author n0nam3
 */
public final class AuctionItem extends ItemInfo
{
	private final int _auctionItemId;
	private final int _auctionLength;
	private final long _auctionInitBid;
	
	public AuctionItem(int auctionItemId, int auctionLength, long auctionInitBid, int itemId, long itemCount, boolean altByItem, StatsSet itemExtra)
	{
		_auctionItemId = auctionItemId;
		_auctionLength = auctionLength;
		_auctionInitBid = auctionInitBid;
		
		setObjectId(itemId);
		setItemId(itemId);
		setCount(itemCount);
		setEquipped(altByItem);
		setEnchantLevel(itemExtra.getInteger("enchant_level", 0));
		setAugmentationId(itemExtra.getInteger("augmentation_id", 0));
	}
	
	public final int getAuctionItemId()
	{
		return _auctionItemId;
	}
	
	public final int getAuctionLength()
	{
		return _auctionLength;
	}
	
	public final long getAuctionInitBid()
	{
		return _auctionInitBid;
	}
	
	public final ItemInstance createNewItemInstance()
	{
		final ItemInstance item = ItemFunctions.createItem(getItemId());
		item.setEnchantLevel(getEnchantLevel());
		if (getAugmentationId() != 0)
		{
			item.setAugmentationId(getAugmentationId());
		}
		
		return item;
	}
}