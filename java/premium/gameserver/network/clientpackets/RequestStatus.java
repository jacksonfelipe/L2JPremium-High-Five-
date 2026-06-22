package premium.gameserver.network.clientpackets;

import premium.gameserver.network.serverpackets.SendStatus;

public final class RequestStatus extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		this.getClient().close(new SendStatus());
	}
}