package premium.gameserver.network.clientpackets;

import premium.gameserver.Config;
import premium.gameserver.instancemanager.games.MiniGameScoreManager;
import premium.gameserver.model.Player;

public class RequestBR_MiniGameInsertScore extends L2GameClientPacket
{
	private int _score;
	
	@Override
	protected void readImpl()
	{
		this._score = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || !Config.EX_JAPAN_MINIGAME)
		{
			return;
		}
		
		MiniGameScoreManager.getInstance().insertScore(player, this._score);
	}
}