package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.ResidenceHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.residence.Fortress;
import premium.gameserver.network.serverpackets.ExShowFortressMapInfo;

public class RequestFortressMapInfo extends L2GameClientPacket
{
	private int _fortressId;
	
	@Override
	protected void readImpl()
	{
		this._fortressId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		Fortress fortress = ResidenceHolder.getInstance().getResidence(Fortress.class, this._fortressId);
		if (fortress != null)
		{
			this.sendPacket(new ExShowFortressMapInfo(fortress));
		}
	}
}