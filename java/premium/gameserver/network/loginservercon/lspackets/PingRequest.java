package premium.gameserver.network.loginservercon.lspackets;

import premium.gameserver.network.loginservercon.AuthServerCommunication;
import premium.gameserver.network.loginservercon.ReceivablePacket;
import premium.gameserver.network.loginservercon.gspackets.PingResponse;

public class PingRequest extends ReceivablePacket
{
	@Override
	public void readImpl()
	{
		
	}
	
	@Override
	protected void runImpl()
	{
		AuthServerCommunication.getInstance().sendPacket(new PingResponse());
	}
}