package premium.gameserver.handler.admincommands.impl;

import premium.gameserver.handler.admincommands.IAdminCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminHelpPage implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_showhtml
	}
	
	@Override
	public boolean useAdminCommand(@SuppressWarnings("rawtypes") Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		
		if (!activeChar.getPlayerAccess().Menu)
		{
			return false;
		}
		
		switch (command)
		{
			case admin_showhtml:
				if (wordList.length != 2)
				{
					activeChar.sendMessage("Usage: //showhtml <file>");
					return false;
				}
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/" + wordList[1]));
				break;
		}
		
		return true;
	}
	
	public static void showHelpHtml(Player targetChar, String content)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setHtml(content);
		targetChar.sendPacket(adminReply);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}