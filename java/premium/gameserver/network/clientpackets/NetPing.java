package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;

//import premium.gameserver.network.l2.Pinger;

public class NetPing extends L2GameClientPacket
{
	int playerId;
	int ping;
	int mtu;
	
	@Override
	protected void readImpl()
	{
		this.playerId = this.readD();
		this.ping = this.readD();
		this.mtu = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.setPing(this.ping);
	}
	
	@Override
	public String getType()
	{
		return "[C] B1 NetPing";
	}
}