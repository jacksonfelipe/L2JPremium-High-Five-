package premium.gameserver.permission.actor.player;

import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.PlayerPermission;

public interface EnchantItemPermission extends PlayerPermission
{
	boolean canEnchantItem(Player p0, ItemInstance p1, ItemInstance p2, ItemInstance p3);
	
	void sendPermissionDeniedError(Player p0, ItemInstance p1, ItemInstance p2, ItemInstance p3);
}
