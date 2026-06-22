package ai;

import premium.gameserver.ThreadPoolManager;
import premium.gameserver.ai.CharacterAI;
import premium.gameserver.model.Player;
import premium.gameserver.model.World;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.PlaySound;

/**
 * - AI for Music Box (32437). - Plays music. - AI has been tested and works.
 */
public class MusicBox extends CharacterAI
{
	public MusicBox(NpcInstance actor)
	{
		super(actor);
		ThreadPoolManager.getInstance().schedule(new ScheduleMusic(), 1000);
	}
	
	private class ScheduleMusic implements Runnable
	{
		@Override
		public void run()
		{
			NpcInstance actor = (NpcInstance) getActor();
			for (Player player : World.getAroundPlayers(actor, 5000, 5000))
			{
				player.broadcastPacket(new PlaySound("TP04_F"));
			}
		}
	}
}