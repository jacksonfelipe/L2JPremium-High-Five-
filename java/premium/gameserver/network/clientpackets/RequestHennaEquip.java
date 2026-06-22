package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.HennaHolder;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.templates.Henna;

public class RequestHennaEquip extends L2GameClientPacket
{
	private int _symbolId;
	
	/**
	 * packet type id 0x6F format: cd
	 */
	@Override
	protected void readImpl()
	{
		this._symbolId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		Henna temp = HennaHolder.getInstance().getHenna(this._symbolId);
		if (temp == null || !temp.isForThisClass(player))
		{
			player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
			return;
		}
		
		long adena = player.getAdena();
		long countDye = player.getInventory().getCountOf(temp.getDyeId());
		
		if (countDye >= temp.getDrawCount() && adena >= temp.getPrice())
		{
			if (player.consumeItem(temp.getDyeId(), temp.getDrawCount()) && player.reduceAdena(temp.getPrice(), "RequestHennaEquip"))
			{
				player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_ADDED);
				player.addHenna(temp);
			}
		}
		else
		{
			player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
		}
	}
}