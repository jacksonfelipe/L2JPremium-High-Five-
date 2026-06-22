package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import premium.gameserver.scripts.Functions;

public class Online extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"online"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		String answer = CCPSmallCommands.showOnlineCount();
		if (answer != null)
		{
			activeChar.sendMessage(answer);
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}