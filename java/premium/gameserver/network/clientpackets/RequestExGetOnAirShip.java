package premium.gameserver.network.clientpackets;

import premium.gameserver.utils.Location;

//@Deprecated
public class RequestExGetOnAirShip extends L2GameClientPacket
{
	public int _shipId;
	private Location loc = new Location();
	
	@Override
	protected void readImpl()
	{
		this.loc.x = this.readD();
		this.loc.y = this.readD();
		this.loc.z = this.readD();
		this._shipId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
	 
	}
}