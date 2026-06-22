package premium.gameserver.permission.actor.player;

import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.PlayablePermission;

public interface AttributeItemPermission extends PlayablePermission
{
	boolean canAttributeItem(Player p0, ItemInstance p1, ItemInstance p2);
	
	void sendPermissionDeniedError(Player p0, ItemInstance p1, ItemInstance p2);
}
