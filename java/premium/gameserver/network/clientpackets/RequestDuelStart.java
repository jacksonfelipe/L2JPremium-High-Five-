package premium.gameserver.network.clientpackets;

import premium.gameserver.Config;
import premium.gameserver.data.xml.holder.EventHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.World;
import premium.gameserver.model.entity.events.EventType;
import premium.gameserver.model.entity.events.impl.DuelEvent;
import premium.gameserver.network.serverpackets.SystemMessage2;
import premium.gameserver.network.serverpackets.components.SystemMsg;

public class RequestDuelStart extends L2GameClientPacket
{
	private String _name;
	private int _duelType;
	
	@Override
	protected void readImpl()
	{
		this._name = this.readS(Config.CNAME_MAXLEN);
		this._duelType = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.isActionsDisabled())
		{
			player.sendActionFailed();
			return;
		}
		
		if (player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		
		Player target = World.getPlayer(this._name);
		if (target == null || target == player)
		{
			player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
			return;
		}
		
		DuelEvent duelEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, this._duelType);
		if ((duelEvent == null) || !duelEvent.canDuel(player, target, true) || target.isInFightClub())
		{
			return;
		}
		
		if (target.isBusy())
		{
			player.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
			return;
		}
		
		duelEvent.askDuel(player, target);
	}
}