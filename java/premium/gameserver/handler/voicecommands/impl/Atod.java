package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import premium.gameserver.scripts.Functions;

public class Atod extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"openatod",
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		if (command.equalsIgnoreCase("openatod"))
		{
			if (params == null)
			{
				activeChar.sendMessage("Usage: .openatod <num>");
			}
			else
			{
				int num = 0;
				try
				{
					num = Integer.parseInt(params);
				}
				catch (NumberFormatException nfe)
				{
					activeChar.sendMessage("You must enter a number. Usage: .openatod <num>");
					return false;
				}
				
				CCPSmallCommands.openToad(activeChar, num);
			}
			
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
	
}