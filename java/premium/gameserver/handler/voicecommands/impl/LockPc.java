package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.data.htm.HtmCache;
import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CharacterControlPanel;
import premium.gameserver.scripts.Functions;

public class LockPc extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"lock"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		String nextPage = CharacterControlPanel.getInstance().useCommand(activeChar, "hwidPage", "-h user_control ");
		
		if (nextPage == null || nextPage.isEmpty())
		{
			return true;
		}
		String html = "command/" + nextPage;
		
		String dialog = HtmCache.getInstance().getNotNull(html, activeChar);
		
		dialog = CharacterControlPanel.getInstance().replacePage(dialog, activeChar, "", "-h user_control ");
		
		show(dialog, activeChar);
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
	
}
