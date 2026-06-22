package premium.gameserver.network.loginservercon.gspackets;

import premium.gameserver.network.loginservercon.SendablePacket;

public class ChangeAllowedHwid extends SendablePacket
{
	private final String account;
	private final String hwid;
	
	public ChangeAllowedHwid(String account, String hwid)
	{
		this.account = account;
		this.hwid = hwid;
	}
	
	@Override
	protected void writeImpl()
	{
		this.writeC(0x09);
		this.writeS(this.account);
		this.writeS(this.hwid);
	}
}