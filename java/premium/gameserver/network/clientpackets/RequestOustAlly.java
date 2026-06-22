package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.pledge.Alliance;
import premium.gameserver.model.pledge.Clan;
import premium.gameserver.network.serverpackets.components.CustomMessage;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.tables.ClanTable;

public class RequestOustAlly extends L2GameClientPacket
{
	private String _clanName;
	
	@Override
	protected void readImpl()
	{
		this._clanName = this.readS(32);
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Clan leaderClan = activeChar.getClan();
		if (leaderClan == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		Alliance alliance = leaderClan.getAlliance();
		if (alliance == null)
		{
			activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
			return;
		}
		
		Clan clan;
		
		if (!activeChar.isAllyLeader())
		{
			activeChar.sendPacket(SystemMsg.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
			return;
		}
		
		if (this._clanName == null)
		{
			return;
		}
		
		clan = ClanTable.getInstance().getClanByName(this._clanName);
		if (clan != null)
		{
			if (!alliance.isMember(clan.getClanId()))
			{
				activeChar.sendActionFailed();
				return;
			}
			
			if (alliance.getLeader().equals(clan))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE);
				return;
			}
			
			for (Player member : clan.getOnlineMembers())
			{
				member.sendMessage("Your clan has been expelled from " + alliance.getAllyName() + " alliance.");
			}
			clan.setAllyId(0);
			clan.setLeavedAlly();
			alliance.broadcastAllyStatus();
			alliance.removeAllyMember(clan.getClanId());
			alliance.setExpelledMember();
			activeChar.sendMessage(new CustomMessage("premium.gameserver.clientpackets.RequestOustAlly.ClanDismissed", activeChar).addString(clan.getName()).addString(alliance.getAllyName()));
		}
	}
}