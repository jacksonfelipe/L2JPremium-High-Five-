package premium.gameserver.model.items;

import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance.ItemLocation;

public class PcWarehouse extends Warehouse
{
	public PcWarehouse(Player owner)
	{
		super(owner.getObjectId());
	}
	
	public PcWarehouse(int ownerId)
	{
		super(ownerId);
	}
	
	@Override
	public ItemLocation getItemLocation()
	{
		return ItemLocation.WAREHOUSE;
	}
}