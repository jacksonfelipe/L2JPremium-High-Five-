package premium.gameserver.model.instances;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.gameserver.instancemanager.RaidBossSpawnManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.Spawner;
import premium.gameserver.network.serverpackets.ExShowQuestInfo;
import premium.gameserver.network.serverpackets.RadarControl;
import premium.gameserver.network.serverpackets.components.CustomMessage;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.Location;

public class AdventurerInstance extends NpcInstance
{
	private static final Logger _log = LoggerFactory.getLogger(AdventurerInstance.class);
	
	public AdventurerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		
		if (command.startsWith("npcfind_byid"))
		{
			try
			{
				int bossId = Integer.parseInt(command.substring(12).trim());
				switch (RaidBossSpawnManager.getInstance().getRaidBossStatusId(bossId))
				{
					case ALIVE:
					case DEAD:
						Spawner spawn = RaidBossSpawnManager.getInstance().getSpawnTable().get(bossId);
						
						Location loc = spawn.getCurrentSpawnRange().getRandomLoc(spawn.getReflection().getGeoIndex());
						
						// Убираем и ставим флажок на карте и стрелку на компасе
						player.sendPacket(new RadarControl(2, 2, loc), new RadarControl(0, 1, loc));
						break;
					case UNDEFINED:
						player.sendMessage(new CustomMessage("premium.gameserver.model.instances.L2AdventurerInstance.BossNotInGame", player).addNumber(bossId));
						break;
				}
			}
			catch (NumberFormatException e)
			{
				_log.warn("AdventurerInstance: Invalid Bypass to Server command parameter.");
			}
		}
		else if (command.startsWith("raidInfo"))
		{
			int bossLevel = Integer.parseInt(command.substring(9).trim());
			
			String filename = "adventurer_guildsman/raid_info/info.htm";
			if (bossLevel != 0)
			{
				filename = "adventurer_guildsman/raid_info/level" + bossLevel + ".htm";
			}
			
			showChatWindow(player, filename);
		}
		else if (command.equalsIgnoreCase("questlist"))
		{
			player.sendPacket(ExShowQuestInfo.STATIC);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "adventurer_guildsman/" + pom + ".htm";
	}
}