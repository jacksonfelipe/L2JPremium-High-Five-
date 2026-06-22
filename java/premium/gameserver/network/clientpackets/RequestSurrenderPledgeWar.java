package premium.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.gameserver.model.Player;
import premium.gameserver.model.pledge.Clan;
import premium.gameserver.model.pledge.ClanWar;
import premium.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import premium.gameserver.network.serverpackets.SystemMessage2;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.tables.ClanTable;

/**
 * @author GodWorld & reworked by Bonux
 **/
public final class RequestSurrenderPledgeWar extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestSurrenderPledgeWar.class);
	
	private String _pledgeName;
	
	@Override
	protected void readImpl()
	{
		this._pledgeName = this.readS();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}
		
		Clan targetClan = ClanTable.getInstance().getClanByName(this._pledgeName);
		if (targetClan == null)
		{
			activeChar.sendPacket(SystemMsg.THE_TARGET_FOR_DECLARATION_IS_WRONG);
			activeChar.sendActionFailed();
			return;
		}
		
		_log.info(this.getClass().getSimpleName() + ": by " + clan.getName() + " with " + this._pledgeName);
		
		if (!clan.isAtWarWith(targetClan.getClanId()))
		{
			// TODO: activeChar.sendMessage("You aren't at war with this clan.");
			activeChar.sendActionFailed();
			return;
		}
		
		activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN).addString(this._pledgeName));
		// activeChar.deathPenalty(false, false, false);
		
		ClanWar war = clan.getClanWar(targetClan);
		if (war != null)
		{
			war.setPeriod(ClanWarPeriod.PEACE);
		}
	}
}