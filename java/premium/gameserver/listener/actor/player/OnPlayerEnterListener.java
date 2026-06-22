package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnPlayerEnterListener extends PlayerListener
{
	public void onPlayerEnter(Player player);
}
