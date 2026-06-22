package premium.gameserver.handler.petition;

import premium.gameserver.model.Player;

public interface IPetitionHandler
{
	void handle(Player player, int id, String txt);
}
