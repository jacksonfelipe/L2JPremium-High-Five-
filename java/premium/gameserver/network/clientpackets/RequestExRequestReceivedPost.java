package premium.gameserver.network.clientpackets;

import premium.commons.dao.JdbcEntityState;
import premium.gameserver.dao.MailDAO;
import premium.gameserver.model.Player;
import premium.gameserver.model.mail.Mail;
import premium.gameserver.network.serverpackets.ExChangePostState;
import premium.gameserver.network.serverpackets.ExReplyReceivedPost;
import premium.gameserver.network.serverpackets.ExShowReceivedPostList;

/**
 * Запрос информации об полученном письме. Появляется при нажатии на письмо из списка {@link ExShowReceivedPostList}.
 * @see RequestExRequestSentPost
 */
public class RequestExRequestReceivedPost extends L2GameClientPacket
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
		
		Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), this.postId);
		if (mail != null)
		{
			if (mail.isUnread())
			{
				mail.setUnread(false);
				mail.setJdbcState(JdbcEntityState.UPDATED);
				mail.update();
				activeChar.sendPacket(new ExChangePostState(true, Mail.READED, mail));
			}
			
			activeChar.sendPacket(new ExReplyReceivedPost(mail));
			return;
		}
		
		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}