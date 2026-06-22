package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.HennaHolder;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.HennaItemInfo;
import premium.gameserver.templates.Henna;

public class RequestHennaItemInfo extends L2GameClientPacket
{
	// format cd
	private int _symbolId;
	
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
		
		Henna henna = HennaHolder.getInstance().getHenna(this._symbolId);
		if (henna != null)
		{
			player.sendPacket(new HennaItemInfo(henna, player));
		}
	}
}