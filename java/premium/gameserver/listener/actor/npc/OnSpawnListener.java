package premium.gameserver.listener.actor.npc;

import premium.gameserver.listener.NpcListener;
import premium.gameserver.model.instances.NpcInstance;

public interface OnSpawnListener extends NpcListener
{
	public void onSpawn(NpcInstance actor);
}
