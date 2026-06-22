package premium.gameserver.permission.actor.player;

import premium.gameserver.model.Player;
import premium.gameserver.permission.PlayerPermission;

public interface LogOutPermission extends PlayerPermission
{
	boolean canLogOut(Player p0, boolean p1);
	
	void sendPermissionDeniedError(Player p0, boolean p1);
}
