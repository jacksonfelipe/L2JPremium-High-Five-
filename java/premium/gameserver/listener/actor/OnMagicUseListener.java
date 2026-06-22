package premium.gameserver.listener.actor;

import premium.gameserver.listener.CharListener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;

public interface OnMagicUseListener extends CharListener
{
	public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt);
}
