package premium.gameserver.network.clientpackets;

import premium.gameserver.Config;
import premium.gameserver.model.Player;
import premium.gameserver.network.GameClient;
import premium.gameserver.network.GameClient.GameClientState;
import premium.gameserver.network.serverpackets.ActionFail;
import premium.gameserver.network.serverpackets.CharSelected;
import premium.gameserver.utils.AutoBan;

public class CharacterSelected extends L2GameClientPacket
{
	private int _charSlot;
	
	/**
	 * Format: cdhddd
	 */
	@Override
	protected void readImpl()
	{
		this._charSlot = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		GameClient client = this.getClient();
		
		if (Config.SECOND_AUTH_ENABLED && !client.getSecondaryAuth().isAuthed())
		{
			client.getSecondaryAuth().openDialog();
			return;
		}
		
		if (client.getActiveChar() != null)
		{
			return;
		}
		
		int objId = client.getObjectIdForSlot(this._charSlot);
		if (AutoBan.checkIsBanned(objId))
		{
			this.sendPacket(ActionFail.STATIC);
			return;
		}
		
		Player activeChar = client.loadCharFromDisk(this._charSlot, objId);
		if (activeChar == null)
		{
			this.sendPacket(ActionFail.STATIC);
			return;
		}
		
		if (activeChar.getAccessLevel() < 0)
		{
			activeChar.setAccessLevel(0);
		}
		client.setState(GameClientState.ENTER_GAME);
		this.sendPacket(new CharSelected(activeChar, client.getSessionKey().playOkID1));
	}
}