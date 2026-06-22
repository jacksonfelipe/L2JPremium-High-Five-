package npc.model;

import premium.gameserver.cache.Msg;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.ItemFunctions;

/**
 * @author pchayka
 */

public final class KeplonInstance extends NpcInstance
{
	public KeplonInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this) || checkForDominionWard(player))
		{
			return;
		}
		
		if (command.equalsIgnoreCase("buygreen"))
		{
			if (ItemFunctions.removeItem(player, 57, 10000, true, "KeplonInstance") >= 10000)
			{
				ItemFunctions.addItem(player, 4401, 1, true, "KeplonInstance");
				return;
			}
			else
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if (command.startsWith("buyblue"))
		{
			if (ItemFunctions.removeItem(player, 57, 10000, true, "KeplonInstance") >= 10000)
			{
				ItemFunctions.addItem(player, 4402, 1, true, "KeplonInstance");
				return;
			}
			else
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if (command.startsWith("buyred"))
		{
			if (ItemFunctions.removeItem(player, 57, 10000, true, "KeplonInstance") >= 10000)
			{
				ItemFunctions.addItem(player, 4403, 1, true, "KeplonInstance");
				return;
			}
			else
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}