package premium.gameserver.permission.actor;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.permission.CharPermission;

public interface IgnoreAttackBlockadesPermission extends CharPermission
{
	boolean canIgnoreAttackBlockades(Creature p0, Creature p1, Skill p2, boolean p3);
}
