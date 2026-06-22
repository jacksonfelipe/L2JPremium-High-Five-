package premium.gameserver.handler.voicecommands.impl;

import premium.gameserver.handler.voicecommands.IVoicedCommandHandler;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.CCPHelpers.itemLogs.CCPItemLogs;
import premium.gameserver.utils.Log;
import premium.gameserver.utils.Util;

public class ItemLogsVoice implements IVoicedCommandHandler
{
	private static final String[] _commandList =
	{
		"itemlogs"
	};
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		final String[] argsSplit = args.split("_");
		if (argsSplit.length != 2)
		{
			CCPItemLogs.showPage(player, player, Util.getInteger(args, 0));
		}
		else
		{
			final Player target = GameObjectsStorage.getPlayer(Integer.parseInt(argsSplit[0]));
			if (target == null)
			{
				CCPItemLogs.showPage(player, player, Util.getInteger(argsSplit[1], 0));
			}
			else if (target.getObjectId() != player.getObjectId() && !player.isGM())
			{
				Log.logIllegalActivity(player.toString() + " Hacked Item Logs Bypass. Args: " + args);
				CCPItemLogs.showPage(player, player, Util.getInteger(argsSplit[1], 0));
			}
			else
			{
				CCPItemLogs.showPage(player, target, Util.getInteger(argsSplit[1], 0));
			}
		}
		return true;
	}
}