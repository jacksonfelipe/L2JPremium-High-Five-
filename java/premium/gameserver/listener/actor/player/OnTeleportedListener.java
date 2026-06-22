package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnTeleportedListener extends PlayerListener
{
	void onTeleported(Player p0);
}
