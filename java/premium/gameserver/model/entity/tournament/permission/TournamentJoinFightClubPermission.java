package premium.gameserver.model.entity.tournament.permission;

import premium.gameserver.data.StringHolder;
import premium.gameserver.model.Player;
import premium.gameserver.permission.actor.player.JoinFightClubPermission;

public class TournamentJoinFightClubPermission implements JoinFightClubPermission
{
	@Override
	public boolean joinSignFightClub(Player actor)
	{
		return false;
	}
	
	@Override
	public String getPermissionDeniedError(Player actor)
	{
		return StringHolder.getNotNull(actor, "Tournament.NotAllowed.JoinFightClub", new Object[0]);
	}
}
