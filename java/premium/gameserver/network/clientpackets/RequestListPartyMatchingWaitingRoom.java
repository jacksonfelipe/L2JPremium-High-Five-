package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

/**
 * @author VISTALL
 */
public class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket
{
	private int _minLevel, _maxLevel, _page, _classes[];
	
	@Override
	protected void readImpl()
	{
		this._page = this.readD();
		this._minLevel = this.readD();
		this._maxLevel = this.readD();
		int size = this.readD();
		if (size > Byte.MAX_VALUE || size < 0)
		{
			size = 0;
		}
		this._classes = new int[size];
		for (int i = 0; i < size; i++)
		{
			this._classes[i] = this.readD();
		}
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, this._minLevel, this._maxLevel, this._page, this._classes));
	}
}