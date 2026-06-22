package handler.items;

import java.util.List;

import premium.gameserver.Config;
import premium.gameserver.cache.Msg;
import premium.gameserver.handler.items.ItemHandler;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.model.reward.RewardData;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.scripts.ScriptFile;
import premium.gameserver.tables.FishTable;
import premium.gameserver.utils.ItemFunctions;
import premium.gameserver.utils.Util;

public class FishItem extends ScriptItemHandler implements ScriptFile
{
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
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;
		
		if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10)
		{
			player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			return false;
		}
		
		if (!player.getInventory().destroyItem(item, 1L, "FishItem"))
		{
			player.sendActionFailed();
			return false;
		}
		
		int count = 0;
		List<RewardData> rewards = FishTable.getInstance().getFishReward(item.getItemId());
		for (RewardData d : rewards)
		{
			long roll = Util.rollDrop(d.getMinDrop(), d.getMaxDrop(), d.getChance() * Config.RATE_FISH_DROP_COUNT * Config.RATE_DROP_ITEMS * player.getRateItems(), false);
			if (roll > 0)
			{
				ItemFunctions.addItem(player, d.getItemId(), roll, true, "FishItem");
				count++;
			}
		}
		if (count == 0)
		{
			player.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
		}
		return true;
	}
	
	@Override
	public int[] getItemIds()
	{
		return FishTable.getInstance().getFishIds();
	}
}