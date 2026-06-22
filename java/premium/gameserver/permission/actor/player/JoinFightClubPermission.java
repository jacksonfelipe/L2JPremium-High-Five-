package premium.gameserver.permission.actor.player;

import premium.gameserver.model.Player;
import premium.gameserver.permission.PlayerPermission;

public interface JoinFightClubPermission extends PlayerPermission
{
	boolean joinSignFightClub(Player p0);
	
	default void sendPermissionDeniedError(Player actor)
	{
		actor.sendMessage(getPermissionDeniedError(actor));
	}
	
	String getPermissionDeniedError(Player p0);
}
