package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.EventHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.Request;
import premium.gameserver.model.Request.L2RequestType;
import premium.gameserver.model.entity.events.EventType;
import premium.gameserver.model.entity.events.impl.DuelEvent;
import premium.gameserver.network.serverpackets.SystemMessage2;
import premium.gameserver.network.serverpackets.components.SystemMsg;

public class RequestDuelAnswerStart extends L2GameClientPacket
{
	private int _response;
	private int _duelType;
	
	@Override
	protected void readImpl()
	{
		this._duelType = this.readD();
		this.readD(); // 1 посылается если ниже -1(при включеной опции клиента Отменять дуели)
		this._response = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Request request = activeChar.getRequest();
		if (request == null || !request.isTypeOf(L2RequestType.DUEL))
		{
			return;
		}
		
		if (!request.isInProgress() || activeChar.isActionsDisabled())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}
		
		Player requestor = request.getRequestor();
		if (requestor == null)
		{
			request.cancel();
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			activeChar.sendActionFailed();
			return;
		}
		
		if ((requestor.getRequest() != request) || (this._duelType != request.getInteger("duelType")))
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}
		
		DuelEvent duelEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, this._duelType);
		
		switch (this._response)
		{
			case 0:
				request.cancel();
				if (this._duelType == 1)
				{
					requestor.sendPacket(SystemMsg.THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
				}
				else
				{
					requestor.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_PARTY_DUEL).addName(activeChar));
				}
				break;
			case -1:
				request.cancel();
				requestor.sendPacket(new SystemMessage2(SystemMsg.C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST).addName(activeChar));
				break;
			case 1:
				if (!duelEvent.canDuel(requestor, activeChar, false))
				{
					request.cancel();
					return;
				}
				
				SystemMessage2 msg1, msg2;
				if (this._duelType == 1)
				{
					msg1 = new SystemMessage2(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_TO_A_PARTY_DUEL);
					msg2 = new SystemMessage2(SystemMsg.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY);
				}
				else
				{
					msg1 = new SystemMessage2(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_A_DUEL);
					msg2 = new SystemMessage2(SystemMsg.C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL);
				}
				
				activeChar.sendPacket(msg1.addName(requestor));
				requestor.sendPacket(msg2.addName(activeChar));
				
				try
				{
					duelEvent.createDuel(requestor, activeChar);
				}
				finally
				{
					request.done();
				}
				break;
		}
	}
}