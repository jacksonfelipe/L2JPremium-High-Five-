package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnPlayerClassLevelIncreasedListener extends PlayerListener
{
	void onClassLevelIncreased(Player p0);
}
