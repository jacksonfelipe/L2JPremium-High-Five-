package ai.hellbound;

import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Zone;
import premium.gameserver.model.instances.DoorInstance;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.ReflectionUtils;

public class MasterZelos extends Fighter
{
	private static Zone _zone;
	private static final int[] doors =
	{
		19260054,
		19260053
	};
	
	public MasterZelos(NpcInstance actor)
	{
		super(actor);
		_zone = ReflectionUtils.getZone("[tully1]");
	}
	
	@Override
	protected void onEvtSpawn()
	{
		setZoneInactive();
		super.onEvtSpawn();
		// Doors
		for (int i = 0; i < doors.length; i++)
		{
			DoorInstance door = ReflectionUtils.getDoor(doors[i]);
			door.closeMe();
		}
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		// Doors
		for (int i = 0; i < doors.length; i++)
		{
			DoorInstance door = ReflectionUtils.getDoor(doors[i]);
			door.openMe();
		}
		super.onEvtDead(killer);
		setZoneActive();
	}
	
	public void setZoneActive()
	{
		_zone.setActive(true);
	}
	
	public void setZoneInactive()
	{
		_zone.setActive(false);
	}
}