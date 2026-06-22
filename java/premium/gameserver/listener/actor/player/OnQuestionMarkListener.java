package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;

public interface OnQuestionMarkListener extends PlayerListener
{
	public void onQuestionMarkClicked(Player player, int questionMarkId);
}
