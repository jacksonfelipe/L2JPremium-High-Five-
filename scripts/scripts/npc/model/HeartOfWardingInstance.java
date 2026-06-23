package npc.model;

import bosses.AntharasManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */

public final class HeartOfWardingInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public HeartOfWardingInstance(int objectId, NpcTemplate template)
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
		
		if (command.equalsIgnoreCase("enter_lair"))
		{
			AntharasManager.enterTheLair(player);
			return;
		}
		super.onBypassFeedback(player, command);
	}
}