package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.CCPCWHPrivilages;
import premium.gameserver.scripts.Functions;

public class CWHPrivileges extends Functions implements IVoicedCommandHandler
{
	private static final String[] _commandList = new String[]
	{
		"clan"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		CCPCWHPrivilages.clanMain(activeChar, args);
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}