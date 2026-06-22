package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import premium.gameserver.scripts.Functions;

/**
 * @author claww
 */
public class Ping extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"ping"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		CCPSmallCommands.getPing(activeChar);
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}