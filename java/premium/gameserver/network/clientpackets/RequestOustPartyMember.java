package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Party;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.DimensionalRift;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.network.serverpackets.components.CustomMessage;

public class RequestOustPartyMember extends L2GameClientPacket
{
	// Format: cS
	private String _name;
	
	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
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
		if (party == null || !activeChar.getParty().isLeader(activeChar))
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(new CustomMessage("premium.gameserver.clientpackets.RequestOustPartyMember.CantOutOfGroup", activeChar));
			return;
		}
		
		Player member = party.getPlayerByName(this._name);
		
		if ((member == activeChar) || (member == null))
		{
			activeChar.sendActionFailed();
			return;
		}
		
		Reflection r = party.getReflection();
		
		if (r != null && r instanceof DimensionalRift && member.getReflection().equals(r))
		{
			activeChar.sendMessage(new CustomMessage("premium.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift", activeChar));
		}
		else if (r != null && !(r instanceof DimensionalRift))
		{
			activeChar.sendMessage(new CustomMessage("premium.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon", activeChar));
		}
		else
		{
			party.removePartyMember(member, true, false);
		}
	}
}