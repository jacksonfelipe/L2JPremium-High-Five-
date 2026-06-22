package ai;

import premium.commons.util.Rnd;
import premium.gameserver.ai.DefaultAI;
import premium.gameserver.instancemanager.games.HandysBlockCheckerManager;
import premium.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.entity.BlockCheckerEngine;
import premium.gameserver.model.instances.BlockInstance;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.ExCubeGameChangePoints;
import premium.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import premium.gameserver.utils.ItemFunctions;
import premium.gameserver.utils.Location;

/**
 * @author n0nam3
 */
public class HandysBlock extends DefaultAI
{
	public HandysBlock(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		BlockInstance actor = (BlockInstance) getActor();
		if ((caster == null) || !caster.isPlayer())
		{
			return;
		}
		Player player = caster.getPlayer();
		int arena = player.getBlockCheckerArena();
		if (arena == -1 || arena > 3)
		{
			return;
		}
		
		if (player.getTarget() == actor)
		{
			if (skill.getId() == 5852 || skill.getId() == 5853)
			{
				ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);
				
				if ((holder.getPlayerTeam(player) == 0 && !actor.isRed()) || (holder.getPlayerTeam(player) == 1 && actor.isRed()))
				{
					actor.changeColor();
					increaseTeamPointsAndSend(player, holder.getEvent());
				}
				else
				{
					return;
				}
				
				// 30% chance to drop the event items
				int random = Rnd.get(100);
				// Bond
				if (random > 69 && random <= 84)
				{
					dropItem(actor, 13787, holder.getEvent(), player);
				}
				else if (random > 84)
				{
					dropItem(actor, 13788, holder.getEvent(), player);
				}
			}
		}
	}
	
	public void increaseTeamPointsAndSend(Player player, BlockCheckerEngine eng)
	{
		int team = eng.getHolder().getPlayerTeam(player);
		eng.increasePlayerPoints(player, team);
		
		int timeLeft = (int) ((eng.getStarterTime() - System.currentTimeMillis()) / 1000);
		boolean isRed = eng.getHolder().getRedPlayers().contains(player);
		
		ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints());
		ExCubeGameExtendedChangePoints secretPoints = new ExCubeGameExtendedChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints(), isRed, player, eng.getPlayerPoints(player, isRed));
		
		eng.getHolder().broadCastPacketToTeam(changePoints);
		eng.getHolder().broadCastPacketToTeam(secretPoints);
	}
	
	public void dropItem(NpcInstance block, int id, BlockCheckerEngine eng, Player player)
	{
		ItemInstance drop = ItemFunctions.createItem(id);
		drop.dropToTheGround(block, Location.findPointToStay(block, 50));
		eng.addNewDrop(drop);
	}
}