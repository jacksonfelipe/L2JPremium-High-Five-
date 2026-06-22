package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.pledge.Clan;
import premium.gameserver.model.pledge.UnitMember;
import premium.gameserver.network.serverpackets.components.CustomMessage;

public class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket
{
	// format: (ch)Sd
	private int _powerGrade;
	private String _name;
	
	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
		this._powerGrade = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if ((activeChar == null) || this._powerGrade < Clan.RANK_FIRST || this._powerGrade > Clan.RANK_LAST)
		{
			return;
		}
		
		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}
		
		if ((activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS)
		{
			UnitMember member = activeChar.getClan().getAnyMember(this._name);
			if (member != null)
			{
				if (Clan.isAcademy(member.getPledgeType()))
				{
					activeChar.sendMessage("You cannot change academy member grade.");
					return;
				}
				if (this._powerGrade > 5)
				{
					member.setPowerGrade(clan.getAffiliationRank(member.getPledgeType()));
				}
				else
				{
					member.setPowerGrade(this._powerGrade);
				}
				if (member.isOnline())
				{
					member.getPlayer().sendUserInfo();
				}
			}
			else
			{
				activeChar.sendMessage(new CustomMessage("premium.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.NotBelongClan", activeChar));
			}
		}
		else
		{
			activeChar.sendMessage(new CustomMessage("premium.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.HaveNotAuthority", activeChar));
		}
	}
}