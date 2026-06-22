package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.achievements.Achievements;
import premium.gameserver.scripts.Functions;

public class AchievementsVoice extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS = new String[]
	{
		"achievements",
		"ach"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		Achievements.getInstance().onBypass(activeChar, "_bbs_achievements", null);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}