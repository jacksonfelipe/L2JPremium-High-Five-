package npc.model;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.MagicSkillUse;
import premium.gameserver.tables.SkillTable;
import premium.gameserver.templates.npc.NpcTemplate;

/**
 * @author claww
 */
public final class NevitHeraldInstance extends NpcInstance
{
	private static final long serialVersionUID = -1L;
	
	public NevitHeraldInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		
		if (command.equalsIgnoreCase("request_blessing"))
		{
			if (player.getEffectList().getEffectsBySkillId(23312) != null)
			{
				showChatWindow(player, 1);
				return;
			}
			List<Creature> target = new ArrayList<>();
			target.add(player);
			broadcastPacket(new MagicSkillUse(this, player, 23312, 1, 0, 0));
			callSkill(SkillTable.getInstance().getInfo(23312, 1), target, true);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}