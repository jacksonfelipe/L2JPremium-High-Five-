package premium.gameserver.network.clientpackets;

import premium.gameserver.network.serverpackets.ExShowCastleInfo;

public class RequestAllCastleInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		this.getClient().getActiveChar().sendPacket(new ExShowCastleInfo());
	}
}