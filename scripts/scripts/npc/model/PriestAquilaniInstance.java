package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.Location;
import quests._10288_SecretMission;

/**
 * @author pchayka
 */
public class PriestAquilaniInstance extends NpcInstance
{
	
	private static final long serialVersionUID = 1L;

	public PriestAquilaniInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (player.getQuestState(_10288_SecretMission.class) != null && player.getQuestState(_10288_SecretMission.class).isCompleted())
		{
			player.sendPacket(new NpcHtmlMessage(player, this, "default/32780-1.htm", val));
			return;
		}
		player.sendPacket(new NpcHtmlMessage(player, this, "default/32780.htm", val));
		return;
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		
		if (command.equalsIgnoreCase("teleport"))
		{
			player.teleToLocation(new Location(118833, -80589, -2688));
			return;
		}
		super.onBypassFeedback(player, command);
	}
}