package premium.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.Announcements;
import premium.gameserver.Config;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.handler.admincommands.IAdminCommandHandler;
import premium.gameserver.model.Player;
import premium.gameserver.model.World;
import premium.gameserver.model.entity.Hero;
import premium.gameserver.model.entity.olympiad.Olympiad;
import premium.gameserver.model.entity.olympiad.OlympiadDatabase;
import premium.gameserver.model.entity.olympiad.OlympiadEndTask;
import premium.gameserver.model.entity.olympiad.OlympiadManager;
import premium.gameserver.model.entity.olympiad.ValidationTask;
import premium.gameserver.network.serverpackets.SystemMessage;
import premium.gameserver.templates.StatsSet;

public class AdminOlympiad implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_oly_save,
		admin_add_oly_points,
		admin_oly_start,
		admin_add_hero,
		admin_oly_stop,
		admin_olympiad_stop_period,
		admin_olympiad_start_period
	}
	
	@Override
	public boolean useAdminCommand(@SuppressWarnings("rawtypes") Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		
		if (activeChar.getPlayerAccess().CanGmEdit)
		{
			switch (command)
			{
				case admin_oly_save:
				{
					if (!Config.ENABLE_OLYMPIAD)
					{
						return false;
					}
					
					try
					{
						OlympiadDatabase.save();
					}
					catch (Exception e)
					{
						
					}
					activeChar.sendMessage("olympaid data saved.");
					break;
				}
				case admin_add_oly_points:
				{
					if (wordList.length < 3)
					{
						activeChar.sendMessage("Command syntax: //add_oly_points <char_name> <point_to_add>");
						activeChar.sendMessage("This command can be applied only for online players.");
						return false;
					}
					
					Player player = World.getPlayer(wordList[1]);
					if (player == null)
					{
						activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
						return false;
					}
					
					int pointToAdd;
					
					try
					{
						pointToAdd = Integer.parseInt(wordList[2]);
					}
					catch (NumberFormatException e)
					{
						activeChar.sendMessage("Please specify integer value for olympiad points.");
						return false;
					}
					
					int curPoints = Olympiad.getNoblePoints(player.getObjectId());
					Olympiad.manualSetNoblePoints(player.getObjectId(), curPoints + pointToAdd);
					int newPoints = Olympiad.getNoblePoints(player.getObjectId());
					
					activeChar.sendMessage("Added " + pointToAdd + " points to character " + player.getName());
					activeChar.sendMessage("Old points: " + curPoints + ", new points: " + newPoints);
					break;
				}
				case admin_oly_start:
				{
					Olympiad._manager = new OlympiadManager();
					Olympiad._inCompPeriod = true;
					
					new Thread(Olympiad._manager).start();
					
					Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.THE_OLYMPIAD_GAME_HAS_STARTED));
					break;
				}
				case admin_oly_stop:
				{
					Olympiad._inCompPeriod = false;
					Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.THE_OLYMPIAD_GAME_HAS_ENDED));
					try
					{
						OlympiadDatabase.save();
					}
					catch (Exception e)
					{
						
					}
					
					break;
				}
				case admin_add_hero:
				{
					if (wordList.length < 2)
					{
						activeChar.sendMessage("Command syntax: //add_hero <char_name>");
						activeChar.sendMessage("This command can be applied only for online players.");
						return false;
					}
					
					Player player = World.getPlayer(wordList[1]);
					if (player == null)
					{
						activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
						return false;
					}
					
					StatsSet hero = new StatsSet();
					hero.set(Olympiad.CLASS_ID, player.getBaseClassId());
					hero.set(Olympiad.CHAR_ID, player.getObjectId());
					hero.set(Olympiad.CHAR_NAME, player.getName());
					
					List<StatsSet> heroesToBe = new ArrayList<>();
					heroesToBe.add(hero);
					
					Hero.getInstance().computeNewHeroes(heroesToBe);
					
					activeChar.sendMessage("Hero status added to player " + player.getName());
					break;
				}
				case admin_olympiad_stop_period:
				{
					Olympiad.cancelPeriodTasks();
					ThreadPoolManager.getInstance().execute(new OlympiadEndTask());
					break;
				}
				case admin_olympiad_start_period:
				{
					Olympiad.cancelPeriodTasks();
					ThreadPoolManager.getInstance().execute(new ValidationTask());
					break;
				}
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}