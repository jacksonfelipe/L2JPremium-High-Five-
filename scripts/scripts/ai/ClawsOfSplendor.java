package ai;

import premium.commons.util.Rnd;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Fighter;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.StatusUpdate;

public class ClawsOfSplendor extends Fighter
{
	public ClawsOfSplendor(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker == null || actor.isDead())
		{
			return;
		}
		
		int transformer = 21522;
		if (transformer > 0)
		{
			int chance = actor.getParameter("transformChance", 90);
			if (chance == 100 || ((MonsterInstance) actor).getChampion() == 0 && actor.getCurrentHpPercents() > 50 && Rnd.chance(chance))
			{
				MonsterInstance npc = (MonsterInstance) NpcHolder.getInstance().getTemplate(transformer).getNewInstance();
				npc.setSpawnedLoc(actor.getLoc());
				npc.setReflection(actor.getReflection());
				npc.setChampion(((MonsterInstance) actor).getChampion());
				npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
				npc.spawnMe(npc.getSpawnedLoc());
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
				actor.doDie(actor);
				actor.decayMe();
				attacker.setTarget(npc);
				attacker.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
				return;
			}
		}
		super.onEvtAttacked(attacker, damage);
	}
}