package premium.gameserver.listener.actor;

import premium.gameserver.listener.CharListener;
import premium.gameserver.model.Creature;

public interface OnStatusUpdateBroadcastListener extends CharListener
{
	void onStatusUpdate(Creature p0);
}
