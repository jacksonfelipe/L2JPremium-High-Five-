package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class BatracosInstance extends NpcInstance
{
	private static final int urogosIzId = 505;
	
	public BatracosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (val == 0)
		{
			String htmlpath = null;
			if (getReflection().isDefault())
			{
				htmlpath = "default/32740.htm";
			}
			else
			{
				htmlpath = "default/32740-4.htm";
			}
			player.sendPacket(new NpcHtmlMessage(player, this, htmlpath, val));
		}
		else
		{
			super.showChatWindow(player, val);
		}
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		
		if (command.equalsIgnoreCase("request_seer"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(urogosIzId))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(urogosIzId))
			{
				ReflectionUtils.enterReflection(player, urogosIzId);
			}
		}
		else if (command.equalsIgnoreCase("leave"))
		{
			if (!getReflection().isDefault())
			{
				getReflection().collapse();
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}