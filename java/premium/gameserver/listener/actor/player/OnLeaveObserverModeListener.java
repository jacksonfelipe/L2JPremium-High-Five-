package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnLeaveObserverModeListener extends PlayerListener
{
	void onLeaveObserverMode(Player p0);
}
