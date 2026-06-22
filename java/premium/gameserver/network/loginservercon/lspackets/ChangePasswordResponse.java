package premium.gameserver.network.loginservercon.lspackets;

import premium.gameserver.model.Player;
import premium.gameserver.network.GameClient;
import premium.gameserver.network.loginservercon.AuthServerCommunication;
import premium.gameserver.network.loginservercon.ReceivablePacket;

public class ChangePasswordResponse extends ReceivablePacket
{
	String account;
	boolean changed;
	
	@Override
	public void readImpl()
	{
		this.account = this.readS();
		this.changed = this.readD() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = AuthServerCommunication.getInstance().getAuthedClient(this.account);
		if (client == null)
		{
			return;
		}
		final Player activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (this.changed)
		{
			activeChar.sendMessage("Your password has been changed!");
			
			// Log.LogAction(activeChar, "Password", "Successfully changed password!");
		}
		else
		{
			activeChar.sendMessage("Current password is incorrect!");
			
			// Log.LogAction(activeChar, "Password", "!!!!!!!! UNSUCCESSFULLY TRIED TO CHANGE PASSWORD IN GAME!!!!!!!!");
		}
	}
}