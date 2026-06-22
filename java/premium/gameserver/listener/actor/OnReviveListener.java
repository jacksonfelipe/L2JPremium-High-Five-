package premium.gameserver.listener.actor;

import premium.gameserver.listener.CharListener;
import premium.gameserver.model.Creature;

/**
 * @author VISTALL
 */
public interface OnReviveListener extends CharListener
{
	public void onRevive(Creature actor);
}
