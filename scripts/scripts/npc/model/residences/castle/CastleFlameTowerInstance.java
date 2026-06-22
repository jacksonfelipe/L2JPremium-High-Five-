package npc.model.residences.castle;

import java.util.List;
import java.util.Set;

import premium.gameserver.model.Creature;
import premium.gameserver.model.entity.events.impl.CastleSiegeEvent;
import premium.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import premium.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 8:58/17.03.2011
 */
public class CastleFlameTowerInstance extends SiegeToggleNpcInstance
{
	private Set<String> _zoneList;
	
	public CastleFlameTowerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onDeathImpl(Creature killer)
	{
		CastleSiegeEvent event = getEvent(CastleSiegeEvent.class);
		if (event == null || !event.isInProgress())
		{
			return;
		}
		
		for (String s : _zoneList)
		{
			List<CastleDamageZoneObject> objects = event.getObjects(s);
			for (CastleDamageZoneObject zone : objects)
			{
				zone.getZone().setActive(false);
			}
		}
	}
	
	@Override
	public void setZoneList(Set<String> set)
	{
		_zoneList = set;
	}
}
