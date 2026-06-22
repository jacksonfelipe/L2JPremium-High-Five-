package premium.gameserver.model.entity.tournament.permission;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.permission.actor.player.ResurrectPermission;

public class TournamentResurrectPermission implements ResurrectPermission
{
	@Override
	public boolean canResurrect(Player actor, Creature target, boolean force, boolean isSalvation)
	{
		return false;
	}
	
	@Override
	public void sendPermissionDeniedError(Player actor, Creature target, boolean force, boolean isSalvation)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.Resurrect", new Object[0]);
	}
}
