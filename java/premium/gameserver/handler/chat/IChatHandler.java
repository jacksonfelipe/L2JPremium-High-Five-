package premium.gameserver.handler.chat;

import premium.gameserver.network.serverpackets.components.ChatType;

public interface IChatHandler
{
	void say();
	
	ChatType getType();
}
