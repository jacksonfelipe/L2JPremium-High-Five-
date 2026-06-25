package premium.gameserver.network.clientpackets;

//@Deprecated
public class RequestExGetOffAirShip extends L2GameClientPacket
{
	public int _x;
	public int _y;
	public int _z;
	public int _id;
	
	@Override
	protected void readImpl()
	{
		this._x = this.readD();
		this._y = this.readD();
		this._z = this.readD();
		this._id = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
	 
	}
}