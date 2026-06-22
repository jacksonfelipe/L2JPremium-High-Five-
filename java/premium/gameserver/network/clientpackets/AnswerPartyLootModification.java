package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Party;
import premium.gameserver.model.Player;

public class AnswerPartyLootModification extends L2GameClientPacket
{
	public int _answer;
	
	@Override
	protected void readImpl()
	{
		this._answer = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Party party = activeChar.getParty();
		if (party != null)
		{
			party.answerLootChangeRequest(activeChar, this._answer == 1);
		}
	}
}
