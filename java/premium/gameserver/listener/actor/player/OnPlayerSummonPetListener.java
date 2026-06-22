package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;
import premium.gameserver.model.Player;
import premium.gameserver.model.Summon;

public interface OnPlayerSummonPetListener extends PlayerListener
{
	void onSummonPet(Player p0, Summon p1);
}
