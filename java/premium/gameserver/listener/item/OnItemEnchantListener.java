package premium.gameserver.listener.item;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;

public interface OnItemEnchantListener extends PlayerListener
{
	public void onEnchantFinish(Player player, ItemInstance item, boolean succeed);
}
