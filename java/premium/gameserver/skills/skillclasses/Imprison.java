package premium.gameserver.skills.skillclasses;

import java.util.List;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.network.serverpackets.Say2;
import premium.gameserver.network.serverpackets.components.ChatType;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.AutoBan;
import premium.gameserver.utils.TimeUtils;

public class Imprison extends Skill
{
	public Imprison(StatsSet set)
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
				if (!target.isPlayer())
				{
					continue;
				}
				
				Player player = target.getPlayer();
				AutoBan.doJailPlayer(player, (int) getPower() * 1000L, false);
				player.sendPacket(new Say2(0, ChatType.TELL, "♦", "Персонаж " + activeChar.getName() + " наложил на Вас проклятие заточения. Вы посажены в тюрьму на срок " + TimeUtils.minutesToFullString((int) getPower() / 60)));
			}
		}
	}
}