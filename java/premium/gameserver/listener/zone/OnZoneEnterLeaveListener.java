package premium.gameserver.listener.zone;

import premium.commons.listener.Listener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener<Zone>
{
	public void onZoneEnter(Zone zone, Creature actor);
	
	public void onZoneLeave(Zone zone, Creature actor);
	
	public void onEquipChanged(Zone zone, Creature actor);
}
