package premium.gameserver.listener.actor;

import premium.gameserver.listener.CharListener;
import premium.gameserver.model.Creature;

public interface OnDeleteListener extends CharListener
{
	void onDelete(Creature p0);
}
