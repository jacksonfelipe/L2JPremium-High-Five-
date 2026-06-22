package premium.gameserver.model.items.attachment;

import premium.gameserver.model.Player;

public interface PickableAttachment extends ItemAttachment
{
	boolean canPickUp(Player player);
	
	void pickUp(Player player);
}
