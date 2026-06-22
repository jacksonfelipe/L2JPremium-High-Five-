package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnPlayerLevelIncreasedListener extends PlayerListener
{
	void onLevelIncreased(Player p0);
}
