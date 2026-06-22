package ai.isle_of_prayer;

import premium.commons.util.Rnd;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Fighter;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.idfactory.IdFactory;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Party;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.model.instances.NpcInstance;

public class IsleOfPrayerFighter extends Fighter
{
	private boolean _penaltyMobsNotSpawned = true;
	private static final int PENALTY_MOBS[] =
	{
		18364,
		18365,
		18366
	};
	private static final int YELLOW_CRYSTAL = 9593;
	private static final int GREEN_CRYSTAL = 9594;
	
	public IsleOfPrayerFighter(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (_penaltyMobsNotSpawned && attacker.isPlayable() && attacker.getPlayer() != null)
		{
			Party party = attacker.getPlayer().getParty();
			if (party != null && party.size() > 2)
			{
				_penaltyMobsNotSpawned = false;
				for (int i = 0; i < 2; i++)
				{
					MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(PENALTY_MOBS[Rnd.get(PENALTY_MOBS.length)]));
					npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
					npc.setReflection(actor.getReflection());
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
					npc.spawnMe(npc.getSpawnedLoc());
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
				}
			}
		}
		
		super.onEvtAttacked(attacker, damage);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		_penaltyMobsNotSpawned = true;
		if (killer != null)
		{
			final Player player = killer.getPlayer();
			if (player != null)
			{
				final NpcInstance actor = getActor();
				switch (actor.getNpcId())
				{
					case 22259: // Muddy Coral
						if (Rnd.chance(26))
						{
							actor.dropItem(player, YELLOW_CRYSTAL, 1);
						}
						break;
					case 22263: // Sonneratia
						if (Rnd.chance(14))
						{
							actor.dropItem(player, GREEN_CRYSTAL, 1);
						}
						break;
				}
			}
		}
		super.onEvtDead(killer);
	}
}