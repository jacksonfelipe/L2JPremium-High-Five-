package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.MerchantInstance;
import premium.gameserver.network.serverpackets.PackageToList;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.WarehouseFunctions;

/**
 * @author VISTALL
 * @date 20:32/16.05.2011
 */
public class FreightSenderInstance extends MerchantInstance
{
	public FreightSenderInstance(int objectId, NpcTemplate template)
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
		
		if (command.equalsIgnoreCase("deposit_items"))
		{
			player.sendPacket(new PackageToList(player));
		}
		else if (command.equalsIgnoreCase("withdraw_items"))
		{
			WarehouseFunctions.showFreightWindow(player);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
