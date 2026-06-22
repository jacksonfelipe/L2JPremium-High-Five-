package premium.gameserver.listener.actor;

import premium.gameserver.listener.CharListener;
import premium.gameserver.model.Creature;

public interface OnAttackListener extends CharListener
{
	public void onAttack(Creature actor, Creature target);
}
