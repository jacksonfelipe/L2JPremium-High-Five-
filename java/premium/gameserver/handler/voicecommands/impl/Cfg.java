package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.data.htm.HtmCache;
import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CharacterControlPanel;
import premium.gameserver.network.serverpackets.ShowBoard;
import premium.gameserver.scripts.Functions;

public class Cfg extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS = new String[]
	{
		"control",
		"cfg"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		String nextPage = CharacterControlPanel.getInstance().useCommand(activeChar, args, "-h user_control ");
		
		if (nextPage == null || nextPage.isEmpty())
		{
			return true;
		}
		
		String html = HtmCache.getInstance().getNotNull("command/" + nextPage, activeChar);
		
		String additionalText = args.split(" ").length > 1 ? args.split(" ")[1] : "";
		html = CharacterControlPanel.getInstance().replacePage(html, activeChar, additionalText, "-h user_control ");
		
		// show(html, activeChar);
		ShowBoard.separateAndSend(html, activeChar);
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}