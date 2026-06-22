package premium.gameserver.network.loginservercon.gspackets;

import premium.gameserver.network.loginservercon.SendablePacket;

public class ChangeAllowedIp extends SendablePacket
{
	private final String account;
	private final String ip;
	
	public ChangeAllowedIp(String account, String ip)
	{
		this.account = account;
		this.ip = ip;
	}
	
	@Override
	protected void writeImpl()
	{
		this.writeC(0x07);
		this.writeS(this.account);
		this.writeS(this.ip);
	}
}