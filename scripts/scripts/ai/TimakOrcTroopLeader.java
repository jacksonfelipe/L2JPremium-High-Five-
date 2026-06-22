package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.commons.util.Rnd;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Fighter;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.components.NpcString;
import premium.gameserver.scripts.Functions;

/**
 * AI для Timak Orc Troop Leader ID: 20767, кричащего и призывающего братьев по клану при ударе.
 * @author SYS
 */
public class TimakOrcTroopLeader extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(TimakOrcTroopLeader.class);
	
	private static final int[] BROTHERS =
	{
		20768, // Timak Orc Troop Shaman
		20769, // Timak Orc Troop Warrior
		20770 // Timak Orc Troop Archer
	};
	
	private boolean _firstTimeAttacked = true;
	
	public TimakOrcTroopLeader(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (!actor.isDead() && _firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			Functions.npcSay(actor, NpcString.SHOW_YOURSELVES);
			for (int bro : BROTHERS)
			{
				try
				{
					NpcInstance npc = NpcHolder.getInstance().getTemplate(bro).getNewInstance();
					npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
					npc.setReflection(actor.getReflection());
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
					npc.spawnMe(npc.getSpawnedLoc());
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
				}
				catch (RuntimeException e)
				{
					LOG.error("Error while spawning brothers of Timak Orc Troop Leader", e);
				}
			}
		}
		super.onEvtAttacked(attacker, damage);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}