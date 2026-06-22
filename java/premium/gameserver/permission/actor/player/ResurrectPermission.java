package premium.gameserver.permission.actor.player;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.permission.PlayerPermission;

public interface ResurrectPermission extends PlayerPermission
{
	boolean canResurrect(Player p0, Creature p1, boolean p2, boolean p3);
	
	void sendPermissionDeniedError(Player p0, Creature p1, boolean p2, boolean p3);
}
