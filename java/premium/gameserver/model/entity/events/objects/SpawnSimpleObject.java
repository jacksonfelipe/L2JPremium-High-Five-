package premium.gameserver.model.entity.events.objects;

import premium.gameserver.model.entity.events.GlobalEvent;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.Location;
import premium.gameserver.utils.NpcUtils;

public class SpawnSimpleObject implements SpawnableObject
{
	private static final long serialVersionUID = 1L;
	private final int _npcId;
	private final Location _loc;
	private NpcInstance _npc;
	
	public SpawnSimpleObject(int npcId, Location loc)
	{
		_npcId = npcId;
		_loc = loc;
	}
	
	@Override
	public void spawnObject(GlobalEvent event)
	{
		_npc = NpcUtils.spawnSingle(_npcId, _loc, event.getReflection());
		if (_npc != null)
		{
			_npc.addEvent(event);
		}
	}
	
	@Override
	public void despawnObject(GlobalEvent event)
	{
		if (_npc != null)
		{
			_npc.removeEvent(event);
			_npc.deleteMe();
			_npc = null;
		}
	}
	
	@Override
	public void refreshObject(GlobalEvent event)
	{
		
	}
}
