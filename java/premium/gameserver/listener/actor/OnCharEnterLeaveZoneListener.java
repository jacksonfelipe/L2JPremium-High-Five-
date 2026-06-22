package premium.gameserver.listener.actor;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Zone;

public interface OnCharEnterLeaveZoneListener
{
	void onEnter(Creature p0, Zone p1);
	
	void onLeave(Creature p0, Zone p1);
}
