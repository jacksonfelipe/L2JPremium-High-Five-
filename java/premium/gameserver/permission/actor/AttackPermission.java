package premium.gameserver.permission.actor;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.network.serverpackets.components.IStaticPacket;
import premium.gameserver.permission.CharPermission;

public interface AttackPermission extends CharPermission
{
	boolean canAttack(Creature p0, Creature p1, Skill p2, boolean p3);
	
	default void sendPermissionDeniedError(Creature actor, Creature target, Skill skill, boolean force)
	{
		actor.sendPacket(getPermissionDeniedError(actor, target, skill, force));
	}
	
	IStaticPacket getPermissionDeniedError(Creature p0, Creature p1, Skill p2, boolean p3);
}
