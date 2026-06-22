package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnPlayerExitListener extends PlayerListener
{
	public void onPlayerExit(Player player);
}
