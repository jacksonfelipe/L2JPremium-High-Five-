package premium.gameserver.network.clientpackets;

import premium.gameserver.dao.SiegeClanDAO;
import premium.gameserver.data.xml.holder.ResidenceHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.events.impl.CastleSiegeEvent;
import premium.gameserver.model.entity.events.objects.SiegeClanObject;
import premium.gameserver.model.entity.residence.Castle;
import premium.gameserver.model.pledge.Clan;
import premium.gameserver.network.serverpackets.CastleSiegeDefenderList;
import premium.gameserver.network.serverpackets.components.SystemMsg;

/**
 * @reworked VISTALL
 */
public class RequestConfirmCastleSiegeWaitingList extends L2GameClientPacket
{
	private boolean _approved;
	private int _unitId;
	private int _clanId;
	
	@Override
	protected void readImpl()
	{
		this._unitId = this.readD();
		this._clanId = this.readD();
		this._approved = this.readD() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}
		
		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this._unitId);
		
		if (castle == null || player.getClan().getCastle() != castle.getId())
		{
			player.sendActionFailed();
			return;
		}
		
		CastleSiegeEvent siegeEvent = castle.getSiegeEvent();
		
		SiegeClanObject siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, this._clanId);
		if (siegeClan == null)
		{
			siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS, this._clanId);
		}
		
		if (siegeClan == null)
		{
			return;
		}
		
		if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST);
			return;
		}
		
		if (siegeEvent.isRegistrationOver())
		{
			player.sendPacket(SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED);
			return;
		}
		
		int allSize = siegeEvent.getObjects(CastleSiegeEvent.DEFENDERS).size();
		if (allSize >= CastleSiegeEvent.MAX_SIEGE_CLANS)
		{
			player.sendPacket(SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
			return;
		}
		
		siegeEvent.removeObject(siegeClan.getType(), siegeClan);
		
		if (this._approved)
		{
			siegeClan.setType(CastleSiegeEvent.DEFENDERS);
		}
		else
		{
			siegeClan.setType(CastleSiegeEvent.DEFENDERS_REFUSED);
		}
		
		siegeEvent.addObject(siegeClan.getType(), siegeClan);
		
		SiegeClanDAO.getInstance().update(castle, siegeClan);
		
		player.sendPacket(new CastleSiegeDefenderList(castle));
	}
}