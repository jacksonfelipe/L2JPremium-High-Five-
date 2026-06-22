package handler.items;

import premium.gameserver.handler.items.ItemHandler;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.SSQStatus;
import premium.gameserver.network.serverpackets.ShowXMasSeal;
import premium.gameserver.scripts.ScriptFile;

public class Books extends SimpleItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[]
	{
		5555,
		5707
	};
	
	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}
	
	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}
	
	@Override
	public void onReload()
	{
		
	}
	
	@Override
	public void onShutdown()
	{
		
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
	
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();
		
		switch (itemId)
		{
			case 5555:
				player.sendPacket(new ShowXMasSeal(5555));
				break;
			case 5707:
				player.sendPacket(new SSQStatus(player, 1));
				break;
			default:
				return false;
		}
		
		return true;
	}
}
