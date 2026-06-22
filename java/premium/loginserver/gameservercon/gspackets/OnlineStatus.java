package premium.loginserver.gameservercon.gspackets;

import premium.loginserver.gameservercon.GameServer;
import premium.loginserver.gameservercon.ReceivablePacket;

/**
 * @author VISTALL
 * @date 21:40/28.06.2011
 */
public class OnlineStatus extends ReceivablePacket
{
	private boolean _online;
	
	@Override
	protected void readImpl()
	{
		_online = readC() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		final GameServer gameServer = getGameServer();
		if (!gameServer.isAuthed())
		{
			return;
		}
		
		gameServer.setOnline(_online);
	}
}
