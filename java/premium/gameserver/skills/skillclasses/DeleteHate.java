package premium.gameserver.skills.skillclasses;

import java.util.List;

import premium.commons.util.Rnd;
import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.components.CustomMessage;
import premium.gameserver.templates.StatsSet;

public class DeleteHate extends Skill
{
	public DeleteHate(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{
				
				if (target.isRaid())
				{
					continue;
				}
				
				if (getActivateRate() > 0)
				{
					if (activeChar.isPlayer() && ((Player) activeChar).isGM())
					{
						activeChar.sendMessage(new CustomMessage("premium.gameserver.skills.Formulas.Chance", (Player) activeChar).addString(getName()).addNumber(getActivateRate()));
					}
					
					if (!Rnd.chance(getActivateRate()))
					{
						return;
					}
				}
				
				if (target.isNpc())
				{
					NpcInstance npc = (NpcInstance) target;
					npc.getAggroList().clear(false);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				}
				
				getEffects(activeChar, target, false, false);
			}
		}
	}
}
