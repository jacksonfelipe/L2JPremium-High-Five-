package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Party;
import premium.gameserver.model.Player;

public class RequestPartyLootModification extends L2GameClientPacket
{
	private byte _mode;
	
	@Override
	protected void readImpl()
	{
		this._mode = (byte) this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if ((activeChar == null) || this._mode < 0 || this._mode > Party.ITEM_ORDER_SPOIL)
		{
			return;
		}
		
		Party party = activeChar.getParty();
		if (party == null || this._mode == party.getLootDistribution() || party.getLeader() != activeChar)
		{
			return;
		}
		
		party.requestLootChange(this._mode);
	}
}
