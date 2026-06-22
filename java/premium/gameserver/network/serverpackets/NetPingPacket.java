package premium.gameserver.network.serverpackets;

import premium.gameserver.model.Player;

/**
 * @author claww
 */
public class NetPingPacket extends L2GameServerPacket
{
	private final int _clientId;
	
	public NetPingPacket(Player cha)
	{
		this._clientId = cha.getObjectId();
	}
	
	@Override
	protected void writeImpl()
	{
		this.writeC(0xD9);
		this.writeD(this._clientId);
	}
}