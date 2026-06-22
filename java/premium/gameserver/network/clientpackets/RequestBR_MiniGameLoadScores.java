package premium.gameserver.network.clientpackets;

import premium.gameserver.Config;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.ExBR_MiniGameLoadScores;

public class RequestBR_MiniGameLoadScores extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || !Config.EX_JAPAN_MINIGAME)
		{
			return;
		}
		
		player.sendPacket(new ExBR_MiniGameLoadScores(player));
	}
}