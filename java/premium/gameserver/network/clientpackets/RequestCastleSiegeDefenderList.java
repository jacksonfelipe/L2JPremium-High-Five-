package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.ResidenceHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.residence.Castle;
import premium.gameserver.network.serverpackets.CastleSiegeDefenderList;

public class RequestCastleSiegeDefenderList extends L2GameClientPacket
{
	private int _unitId;
	
	@Override
	protected void readImpl()
	{
		this._unitId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this._unitId);
		if (castle == null)
		{
			return;
		}
		
		player.sendPacket(new CastleSiegeDefenderList(castle));
	}
}