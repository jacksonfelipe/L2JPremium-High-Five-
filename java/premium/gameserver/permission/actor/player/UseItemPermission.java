package premium.gameserver.permission.actor.player;

import premium.gameserver.model.Playable;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.PlayablePermission;

public interface UseItemPermission extends PlayablePermission
{
	boolean canUseItem(Playable p0, ItemInstance p1, boolean p2);
	
	void sendPermissionDeniedError(Playable p0, ItemInstance p1, boolean p2);
}
