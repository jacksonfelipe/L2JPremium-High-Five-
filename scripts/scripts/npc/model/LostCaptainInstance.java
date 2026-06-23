package npc.model;

import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.idfactory.IdFactory;
import premium.gameserver.model.Creature;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.model.instances.ReflectionBossInstance;
import premium.gameserver.templates.InstantZone;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.Location;

public class LostCaptainInstance extends ReflectionBossInstance
{
	private static final long serialVersionUID = -3233918101387760546L;
	private static final int TELE_DEVICE_ID = 4314;
	
	public LostCaptainInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		Reflection r = getReflection();
		r.setReenterTime(System.currentTimeMillis());
		
		super.onDeath(killer);
		
		InstantZone iz = r.getInstancedZone();
		if (iz != null)
		{
			String tele_device_loc = iz.getAddParams().getString("tele_device_loc", null);
			if (tele_device_loc != null)
			{
				KamalokaGuardInstance npc = new KamalokaGuardInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(TELE_DEVICE_ID));
				npc.setSpawnedLoc(Location.parseLoc(tele_device_loc));
				npc.setReflection(r);
				npc.spawnMe(npc.getSpawnedLoc());
			}
		}
	}
}
