package premium.gameserver.permission.actor.player;

import premium.gameserver.model.Playable;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.PlayablePermission;

public interface LoseItemPermission extends PlayablePermission
{
	boolean canLoseItem(Playable p0, ItemInstance p1);
	
	default void sendPermissionDeniedError(Playable actor, ItemInstance item)
	{
		actor.sendMessage(getPermissionDeniedError(actor, item));
	}
	
	String getPermissionDeniedError(Playable p0, ItemInstance p1);
}
