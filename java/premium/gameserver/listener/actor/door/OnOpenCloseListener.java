package premium.gameserver.listener.actor.door;

import premium.gameserver.listener.CharListener;
import premium.gameserver.model.instances.DoorInstance;

/**
 * @author VISTALL
 * @date 21:03/04.07.2011
 */
public interface OnOpenCloseListener extends CharListener
{
	void onOpen(DoorInstance doorInstance);
	
	void onClose(DoorInstance doorInstance);
}
