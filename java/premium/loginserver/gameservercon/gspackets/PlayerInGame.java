package premium.loginserver.gameservercon.gspackets;

import premium.loginserver.gameservercon.GameServer;
import premium.loginserver.gameservercon.ReceivablePacket;

public class PlayerInGame extends ReceivablePacket
{
	private String account;
	
	@Override
	protected void readImpl()
	{
		account = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final GameServer gs = getGameServer();
		if (gs.isAuthed())
		{
			gs.addAccount(account);
		}
	}
}
