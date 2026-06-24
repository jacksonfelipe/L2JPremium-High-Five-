package premium.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.handler.admincommands.IAdminCommandHandler;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;
import premium.gameserver.scripts.Functions;

public class AdminGiveAll implements IAdminCommandHandler
{
	private static List<String> _l = new ArrayList<>();
	
	enum Commands
	{
		admin_giveall
	}
	
	@Override
	public boolean useAdminCommand(@SuppressWarnings("rawtypes") Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		if (wordList.length >= 3)
		{
			int _id = 0;
			int _count = 0;
			try
			{
				_id = Integer.parseInt(wordList[1]);
				_count = Integer.parseInt(wordList[2]);
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("only numbers");
				return false;
			}
			
			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				if ((player == null) || !checkPlayersIP(player))
				{
					continue;
				}
				Functions.addItem(player, _id, _count, "Give ALl");
				player.sendMessage("You have been rewarded!");
			}
			_l.clear();
		}
		else
		{
			activeChar.sendMessage("use: //giveall itemId count");
			return false;
		}
		return true;
	}
	
	private static boolean checkPlayersIP(Player player)
	{
		if ((player == null) || _l.contains(player.getIP()))
		{
			return false;
		}
		
		_l.add(player.getIP());
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
