package premium.gameserver.model.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import gnu.trove.set.hash.TIntHashSet;
import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.network.serverpackets.MagicSkillUse;
import premium.gameserver.network.serverpackets.MyTargetSelected;
import premium.gameserver.network.serverpackets.ValidateLocation;
import premium.gameserver.scripts.Events;
import premium.gameserver.tables.SkillTable;
import premium.gameserver.templates.npc.NpcTemplate;

public class OlympiadBufferInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;
	private TIntHashSet buffs = new TIntHashSet();
	
	public OlympiadBufferInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onAction(Player player, boolean shift)
	{
		if (Events.onAction(player, this, shift))
		{
			player.sendActionFailed();
			return;
		}
		
		if (this != player.getTarget())
		{
			player.setTarget(this);
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
			if (!isInRange(player, INTERACTION_DISTANCE))
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else if (buffs.size() > 4)
			{
				showChatWindow(player, 1);
			}
			else
			{
				showChatWindow(player, 0);
			}
			player.sendActionFailed();
		}
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		
		if (buffs.size() > 4)
		{
			showChatWindow(player, 1);
		}
		
		if (command.startsWith("Buff"))
		{
			int id = 0;
			int lvl = 0;
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			id = Integer.parseInt(st.nextToken());
			lvl = Integer.parseInt(st.nextToken());
			Skill skill = SkillTable.getInstance().getInfo(id, lvl);
			List<Creature> target = new ArrayList<>();
			target.add(player);
			broadcastPacket(new MagicSkillUse(this, player, id, lvl, 0, 0));
			callSkill(skill, target, true);
			buffs.add(id);
			if (buffs.size() > 4)
			{
				showChatWindow(player, 1);
			}
			else
			{
				showChatWindow(player, 0);
			}
		}
		else
		{
			showChatWindow(player, 0);
		}
	}
	
	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if (val == 0)
		{
			pom = "buffer";
		}
		else
		{
			pom = "buffer-" + val;
		}
		
		// If the file is not found, the standard message "I have nothing to say to you" is returned
		return "olympiad/" + pom + ".htm";
	}
}