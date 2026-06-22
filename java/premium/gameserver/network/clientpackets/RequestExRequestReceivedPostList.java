package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.ExNoticePostArrived;
import premium.gameserver.network.serverpackets.ExShowReceivedPostList;

/**
 * Отсылается при нажатии на кнопку "почта", "received mail" или уведомление от {@link ExNoticePostArrived}, запрос входящих писем. В ответ шлется {@link ExShowReceivedPostList}
 */
public class RequestExRequestReceivedPostList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// just a trigger
	}
	
	@Override
	protected void runImpl()
	{
		Player cha = this.getClient().getActiveChar();
		if (cha != null)
		{
			cha.sendItemList(true);
			cha.sendItemList(false);
			cha.sendPacket(new ExShowReceivedPostList(cha));
		}
		
	}
}