package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.EventHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.events.EventType;
import premium.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import premium.gameserver.network.serverpackets.ExReplyDominionInfo;
import premium.gameserver.network.serverpackets.ExShowOwnthingPos;

public class RequestExDominionInfo extends L2GameClientPacket
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
		
		activeChar.sendPacket(new ExReplyDominionInfo());
		
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		if (runnerEvent.isInProgress())
		{
			activeChar.sendPacket(new ExShowOwnthingPos());
		}
	}
}