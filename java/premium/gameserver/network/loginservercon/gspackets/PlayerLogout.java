package premium.gameserver.network.loginservercon.gspackets;

import premium.gameserver.network.loginservercon.SendablePacket;

public class PlayerLogout extends SendablePacket
{
	private final String account;
	
	public PlayerLogout(String account)
	{
		this.account = account;
	}
	
	@Override
	protected void writeImpl()
	{
		this.writeC(0x04);
		this.writeS(this.account);
	}
}
