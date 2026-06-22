package premium.gameserver.handler.admincommands.impl;

import java.util.List;

import premium.gameserver.handler.admincommands.IAdminCommandHandler;
import premium.gameserver.model.Creature;
import premium.gameserver.model.GameObject;
import premium.gameserver.model.Player;
import premium.gameserver.model.World;
import premium.gameserver.model.base.TeamType;
import premium.gameserver.model.entity.Hero;
import premium.gameserver.network.serverpackets.components.SystemMsg;

public class AdminTeam implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_setteam
	}
	
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		TeamType team = TeamType.NONE;
		int range = -1;
		if (wordList.length >= 2)
		{
			for (TeamType t : TeamType.values())
			{
				if (wordList[1].equalsIgnoreCase(t.name()))
				{
					team = t;
				}
			}
			if (wordList.length >= 3)
			{
				range = Integer.parseInt(wordList[2]);
			}
		}
		
		if (range > 0)
		{
			List<Player> aroundPlayer = World.getAroundPlayers(activeChar, range, 500);
			for (Player player : aroundPlayer)
			{
				player.setTeam(team);
			}
			activeChar.sendMessage("You have changed Team of " + aroundPlayer.size() + " Players!");
		}
		else
		{
			GameObject object = activeChar.getTarget();
			if (object == null || !object.isCreature())
			{
				activeChar.sendPacket(SystemMsg.INVALID_TARGET);
				return false;
			}
			
			((Creature) object).setTeam(team);
			
			if (object.isPlayer())
			{
				Player pObject = object.getPlayer();
				if (pObject.isHero())
				{
					if (team != TeamType.NONE)
					{
						Hero.removeSkills(pObject);
					}
					else
					{
						Hero.addSkills(pObject);
					}
				}
				
				pObject.broadcastRelationChanged();
			}
		}
		return true;
	}
	
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
