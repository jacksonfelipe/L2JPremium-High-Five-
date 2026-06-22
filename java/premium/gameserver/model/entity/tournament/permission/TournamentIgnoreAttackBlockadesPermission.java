package premium.gameserver.model.entity.tournament.permission;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.permission.actor.IgnoreAttackBlockadesPermission;

public class TournamentIgnoreAttackBlockadesPermission implements IgnoreAttackBlockadesPermission
{
	@Override
	public boolean canIgnoreAttackBlockades(Creature actor, Creature target, Skill skill, boolean force)
	{
		return true;
	}
}
