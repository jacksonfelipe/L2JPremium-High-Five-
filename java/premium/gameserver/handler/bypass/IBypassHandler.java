package premium.gameserver.handler.bypass;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;

public interface IBypassHandler
{
	String[] getBypasses();
	
	void onBypassFeedback(NpcInstance npc, Player player, String command);
}
