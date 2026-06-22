package premium.gameserver.network.clientpackets;

import premium.gameserver.Shutdown;
import premium.gameserver.network.GameClient;
import premium.gameserver.network.loginservercon.AuthServerCommunication;
import premium.gameserver.network.loginservercon.SessionKey;
import premium.gameserver.network.loginservercon.gspackets.PlayerAuthRequest;
import premium.gameserver.network.serverpackets.LoginFail;
import premium.gameserver.network.serverpackets.ServerClose;

/**
 * cSddddd cSdddddQ loginName + keys must match what the loginserver used.
 */
public class AuthLogin extends L2GameClientPacket
{
	private String _loginName;
	private int _playKey1;
	private int _playKey2;
	private int _loginKey1;
	private int _loginKey2;
	
	@Override
	protected void readImpl()
	{
		this._loginName = this.readS(32).toLowerCase();
		this._playKey2 = this.readD();
		this._playKey1 = this.readD();
		this._loginKey1 = this.readD();
		this._loginKey2 = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		GameClient client = this.getClient();
		
		SessionKey key = new SessionKey(this._loginKey1, this._loginKey2, this._playKey1, this._playKey2);
		client.setSessionId(key);
		client.setLoginName(this._loginName);
		
		if (Shutdown.getInstance().getMode() != Shutdown.ShutdownMode.NONE && Shutdown.getInstance().getSeconds() <= 15)
		{
			client.closeNow(false);
		}
		else
		{
			if (AuthServerCommunication.getInstance().isShutdown())
			{
				client.close(new LoginFail(LoginFail.SYSTEM_ERROR_LOGIN_LATER));
				return;
			}
			
			GameClient oldClient = AuthServerCommunication.getInstance().addWaitingClient(client);
			if (oldClient != null)
			{
				oldClient.close(ServerClose.STATIC);
			}
			
			AuthServerCommunication.getInstance().sendPacket(new PlayerAuthRequest(client));
		}
	}
}