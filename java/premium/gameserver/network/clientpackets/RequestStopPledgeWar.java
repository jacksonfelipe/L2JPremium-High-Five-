package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.pledge.Clan;
import premium.gameserver.model.pledge.ClanWar;
import premium.gameserver.model.pledge.UnitMember;
import premium.gameserver.network.serverpackets.ActionFail;
import premium.gameserver.network.serverpackets.SystemMessage2;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.tables.ClanTable;

public class RequestStopPledgeWar extends L2GameClientPacket
{
	private String _pledgeName;
	
	@Override
	protected void readImpl()
	{
		this._pledgeName = this.readS(32);
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Clan playerClan = activeChar.getClan();
		if (playerClan == null)
		{
			return;
		}
		
		if (!((activeChar.getClanPrivileges() & Clan.CP_CL_CLAN_WAR) == Clan.CP_CL_CLAN_WAR))
		{
			activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT, ActionFail.STATIC);
			return;
		}
		
		Clan clan = ClanTable.getInstance().getClanByName(this._pledgeName);
		
		if (clan == null)
		{
			activeChar.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID, ActionFail.STATIC);
			return;
		}
		
		if (!playerClan.isAtWarWith(clan.getClanId()))
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_AGAINST_THE_CLAN_S1).addString(clan.getName()), ActionFail.STATIC);
			return;
		}
		
		for (UnitMember mbr : playerClan)
		{
			if (mbr.isOnline() && mbr.getPlayer().isInCombat())
			{
				activeChar.sendPacket(SystemMsg.A_CEASEFIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE, ActionFail.STATIC);
				return;
			}
		}
		
		// ClanTable.getInstance().stopClanWar(playerClan, clan);
		
		ClanWar war = playerClan.getClanWar(clan);
		if (war != null)
		{
			war.cancel(playerClan);
		}
	}
}