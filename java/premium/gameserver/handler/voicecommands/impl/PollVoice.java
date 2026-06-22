package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.CCPPoll;

public class PollVoice implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"poll"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		CCPPoll.bypass(activeChar, target.split(" "));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
	
}
