package premium.gameserver.handler.admincommands.impl;

import premium.gameserver.handler.admincommands.IAdminCommandHandler;
import premium.gameserver.model.Player;

public class AdminScripts implements IAdminCommandHandler
{
	private static enum Commands
	{
	}
	
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		
		if (!activeChar.isGM())
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}