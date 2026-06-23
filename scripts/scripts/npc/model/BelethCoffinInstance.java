package npc.model;

import java.util.StringTokenizer;

import bosses.BelethManager;
import premium.gameserver.model.CommandChannel;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;
import premium.gameserver.network.serverpackets.SystemMessage;
import premium.gameserver.scripts.Functions;
import premium.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */

public final class BelethCoffinInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;
	private static final int RING = 10314;
	
	public BelethCoffinInstance(int objectId, NpcTemplate template)
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
		
		StringTokenizer st = new StringTokenizer(command);
		if (st.nextToken().equals("request_ring"))
		{
			if (!BelethManager.isRingAvailable())
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>Ring is not available. Get lost!"));
				return;
			}
			if (player.getParty() == null || player.getParty().getCommandChannel() == null)
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>You are not allowed to take the ring. Are are not the group or Command Channel."));
				return;
			}
			if (player.getParty().getCommandChannel().getLeader() != player)
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>You are not leader or the Command Channel."));
				return;
			}
			
			CommandChannel channel = player.getParty().getCommandChannel();
			
			Functions.addItem(player, RING, 1, "BelethCoffinInstance");
			
			SystemMessage smsg = new SystemMessage(SystemMessage.S1_HAS_OBTAINED_S2);
			smsg.addString(player.getName());
			smsg.addItemName(RING);
			channel.sendPacket(smsg);
			
			BelethManager.setRingAvailable(false);
			deleteMe();
			
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}