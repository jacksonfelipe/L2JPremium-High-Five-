package ai.monas.FurnaceSpawnRoom;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.DefaultAI;
import premium.gameserver.data.xml.holder.EventHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.entity.events.EventType;
import premium.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.components.NpcString;
import premium.gameserver.scripts.Functions;

public class FurnaceProtection extends DefaultAI
{
	private boolean _firstTimeAttacked = true;
	
	public FurnaceProtection(NpcInstance actor)
	{
		super(actor);
		actor.setNameNpcString(NpcString.FURN3);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}
		
		int event_id = actor.getAISpawnParam();
		MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);
		
		if (_firstTimeAttacked && !furnace.isInProgress())
		{
			_firstTimeAttacked = false;
			attacker.setTarget(null);
			actor.setTargetable(false);
			actor.setNpcState((byte) 1);
			Functions.npcShout(actor, NpcString.FURN1);
			furnace.registerActions();
			ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask(), 15000);
		}
		
		super.onEvtAttacked(attacker, damage);
	}
	
	private class ScheduleTimerTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			int event_id = actor.getAISpawnParam();
			MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);
			furnace.spawnAction(MonasteryFurnaceEvent.PROTECTOR_ROOM, true);
		}
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}