package premium.gameserver.network.loginservercon.lspackets;

import premium.gameserver.cache.Msg;
import premium.gameserver.model.Player;
import premium.gameserver.network.GameClient;
import premium.gameserver.network.loginservercon.AuthServerCommunication;
import premium.gameserver.network.loginservercon.ReceivablePacket;
import premium.gameserver.network.serverpackets.ServerClose;

public class KickPlayer extends ReceivablePacket
{
	String account;
	
	@Override
	public void readImpl()
	{
		this.account = this.readS();
	}
	
	@Override
	protected void runImpl()
	{
		GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(this.account);
		if (client == null)
		{
			client = AuthServerCommunication.getInstance().removeAuthedClient(this.account);
		}
		if (client == null)
		{
			return;
		}
		final Player activeChar = client.getActiveChar();
		if (activeChar != null)
		{
			activeChar.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
			activeChar.kick();
		}
		else
		{
			client.close(ServerClose.STATIC);
		}
	}
}