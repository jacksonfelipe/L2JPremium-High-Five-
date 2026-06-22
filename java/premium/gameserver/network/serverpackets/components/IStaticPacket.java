package premium.gameserver.network.serverpackets.components;

import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.L2GameServerPacket;

public interface IStaticPacket
{
	L2GameServerPacket packet(Player player);
}
