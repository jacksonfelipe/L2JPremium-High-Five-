package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Party;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.DimensionalRift;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.network.serverpackets.components.CustomMessage;

public class RequestWithDrawalParty extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
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
		if (party == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("Вы не можете сейчас выйти из группы."); // TODO [G1ta0] custom message
			return;
		}
		
		Reflection r = activeChar.getParty().getReflection();
		if (r != null && r instanceof DimensionalRift && activeChar.getReflection().equals(r))
		{
			activeChar.sendMessage(new CustomMessage("premium.gameserver.clientpackets.RequestWithDrawalParty.Rift", activeChar));
		}
		else if (r != null && activeChar.isInCombat())
		{
			activeChar.sendMessage("Вы не можете сейчас выйти из группы.");
		}
		else
		{
			activeChar.leaveParty();
		}
	}
}