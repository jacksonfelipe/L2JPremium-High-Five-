package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnFishDieListener extends PlayerListener
{
	void onFishDied(Player player, int fishId, boolean isMonster);
}
