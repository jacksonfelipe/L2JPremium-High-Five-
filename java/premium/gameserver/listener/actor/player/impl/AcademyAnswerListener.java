package premium.gameserver.listener.actor.player.impl;

import premium.commons.lang.reference.HardReference;
import premium.gameserver.multverso.academy.AcademyList;
import premium.gameserver.listener.actor.player.OnAnswerListener;
import premium.gameserver.model.Player;
import premium.gameserver.model.Request;
import premium.gameserver.network.serverpackets.components.ChatType;

public class AcademyAnswerListener implements OnAnswerListener
{
	private final HardReference<Player> _activeChar;
	private final HardReference<Player> _academyChar;
	
	public AcademyAnswerListener(Player activeChar, Player academyChar)
	{
		_activeChar = activeChar.getRef();
		_academyChar = academyChar.getRef();
	}
	
	@Override
	public void sayYes()
	{
		final Player activeChar = _activeChar.get();
		final Player academyChar = _academyChar.get();
		if (activeChar == null || academyChar == null)
		{
			return;
		}
		AcademyList.inviteInAcademy(activeChar, academyChar);
	}
	
	@Override
	public void sayNo()
	{
		final Player activeChar = _activeChar.get();
		final Player academyChar = _academyChar.get();
		if (activeChar == null || academyChar == null)
		{
			return;
		}
		final Request req = activeChar.getRequest();
		if (req != null && req.isInProgress())
		{
			req.cancel();
		}
		activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Player " + academyChar.getName() + " refused to join!");
	}
}
