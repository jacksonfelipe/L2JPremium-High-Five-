package premium.gameserver.network.clientpackets;

import premium.gameserver.dao.MailDAO;
import premium.gameserver.model.Player;
import premium.gameserver.model.mail.Mail;
import premium.gameserver.network.serverpackets.ExReplySentPost;
import premium.gameserver.network.serverpackets.ExShowSentPostList;

/**
 * Запрос информации об отправленном письме. Появляется при нажатии на письмо из списка {@link ExShowSentPostList}. В ответ шлется {@link ExReplySentPost}.
 * @see RequestExRequestReceivedPost
 */
public class RequestExRequestSentPost extends L2GameClientPacket
{
	private int postId;
	
	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		this.postId = this.readD(); // id письма
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), this.postId);
		if (mail != null)
		{
			activeChar.sendPacket(new ExReplySentPost(mail));
			return;
		}
		
		activeChar.sendPacket(new ExShowSentPostList(activeChar));
	}
}