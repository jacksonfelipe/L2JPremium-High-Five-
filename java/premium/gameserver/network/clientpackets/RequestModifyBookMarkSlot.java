package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.actor.instances.player.BookMark;
import premium.gameserver.network.serverpackets.ExGetBookMarkInfo;

/**
 * dSdS
 */
public class RequestModifyBookMarkSlot extends L2GameClientPacket
{
	private String name, acronym;
	private int icon, slot;
	
	@Override
	protected void readImpl()
	{
		this.slot = this.readD();
		this.name = this.readS(32);
		this.icon = this.readD();
		this.acronym = this.readS(4);
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = this.getClient().getActiveChar();
		if (activeChar != null)
		{
			final BookMark mark = activeChar.bookmarks.get(this.slot);
			if (mark != null)
			{
				mark.setName(this.name);
				mark.setIcon(this.icon);
				mark.setAcronym(this.acronym);
				activeChar.sendPacket(new ExGetBookMarkInfo(activeChar));
			}
		}
	}
}