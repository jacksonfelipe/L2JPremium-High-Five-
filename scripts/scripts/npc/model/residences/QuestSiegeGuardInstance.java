package npc.model.residences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import premium.commons.util.Rnd;
import premium.gameserver.Config;
import premium.gameserver.model.AggroList;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.quest.Quest;
import premium.gameserver.model.quest.QuestEventType;
import premium.gameserver.model.quest.QuestState;
import premium.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 19:28/23.06.2011
 */
public class QuestSiegeGuardInstance extends SiegeGuardInstance
{
	private static final long serialVersionUID = 1L;

	public QuestSiegeGuardInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onDeath(Creature lastAttacker)
	{
		super.onDeath(lastAttacker);
		
		Player killer = lastAttacker.getPlayer();
		if (killer == null)
		{
			return;
		}
		
		Map<Playable, AggroList.HateInfo> aggroMap = getAggroList().getPlayableMap();
		
		Quest[] quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
		if (quests != null && quests.length > 0)
		{
			List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
			if (isRaid() && Config.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
			{
				players = new ArrayList<>();
				for (Playable pl : aggroMap.keySet())
				{
					if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
					{
						players.add(pl.getPlayer());
					}
				}
			}
			else if (killer.getParty() != null) // если пати то собираем всех кто подходит
			{
				players = new ArrayList<>(killer.getParty().size());
				for (Player pl : killer.getParty().getMembers())
				{
					if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
					{
						players.add(pl);
					}
				}
			}
			
			for (Quest quest : quests)
			{
				Player toReward = killer;
				if (quest.getParty() != Quest.PARTY_NONE && players != null)
				{
					if (isRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
					{
						for (Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if (qs != null && !qs.isCompleted())
							{
								quest.notifyKill(this, qs);
							}
						}
						toReward = null;
					}
					else
					{ // иначе выбираем одного
						List<Player> interested = new ArrayList<>(players.size());
						for (Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if (qs != null && !qs.isCompleted()) // из тех, у кого взят квест
							{
								interested.add(pl);
							}
						}
						
						if (interested.isEmpty())
						{
							continue;
						}
						
						toReward = interested.get(Rnd.get(interested.size()));
						if (toReward == null)
						{
							toReward = killer;
						}
					}
				}
				
				if (toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest.getName());
					if (qs != null && !qs.isCompleted())
					{
						quest.notifyKill(this, qs);
					}
				}
			}
		}
	}
}
