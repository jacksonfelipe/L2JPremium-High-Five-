package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.CCPSmallCommands;

public class AntiGrief implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"buffshield",
		"buffShield"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		CCPSmallCommands.setAntiGrief(activeChar);
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
	
}
