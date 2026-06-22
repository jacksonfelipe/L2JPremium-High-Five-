package ai.monas.FurnaceSpawnRoom;

import premium.commons.util.Rnd;
import premium.gameserver.ai.DefaultAI;
import premium.gameserver.data.xml.holder.EventHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.entity.events.EventType;
import premium.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import premium.gameserver.model.instances.NpcInstance;

/**
 * @author PaInKiLlEr - AI Monster 22798, 22799, 22800.  * - AI for spawning braziers room.  * - There is a 5% chance that the spawn to death four braziers unlikely.  * - AI is tested and works.
 */
public class DivinityMonster extends DefaultAI
{
	public DivinityMonster(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		
		int event_id = actor.getAISpawnParam();
		MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);
		
		if (Rnd.chance(5) && !furnace.isInProgress())
		{
			furnace.spawnAction(MonasteryFurnaceEvent.FURNACE_ROOM, true);
		}
		
		super.onEvtDead(killer);
	}
}