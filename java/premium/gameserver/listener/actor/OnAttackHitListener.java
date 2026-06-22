package premium.gameserver.listener.actor;

import premium.gameserver.listener.CharListener;
import premium.gameserver.model.Creature;

public interface OnAttackHitListener extends CharListener
{
	public void onAttackHit(Creature actor, Creature attacker);
}
