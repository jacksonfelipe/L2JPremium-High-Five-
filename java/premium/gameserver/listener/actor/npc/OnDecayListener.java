package premium.gameserver.listener.actor.npc;

import premium.gameserver.listener.NpcListener;
import premium.gameserver.model.instances.NpcInstance;

public interface OnDecayListener extends NpcListener
{
	public void onDecay(NpcInstance actor);
}
