package premium.loginserver.gameservercon.gspackets;

import premium.loginserver.gameservercon.ReceivablePacket;
import premium.loginserver.utils.ProxyWaitingList;

public class GameServerProxyResponse extends ReceivablePacket
{
	private String accountName;
	private String proxyIp;
	
	@Override
	protected void readImpl()
	{
		accountName = readS();
		proxyIp = readS();
	}
	
	@Override
	protected void runImpl()
	{
		ProxyWaitingList.getInstance().receiveProxy(getGameServer().getId(), accountName, proxyIp);
	}
}
