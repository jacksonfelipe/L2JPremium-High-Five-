package premium.gameserver.network.serverpackets;

import premium.gameserver.model.entity.boat.Boat;
import premium.gameserver.utils.Location;

public class ExStopMoveAirShip extends L2GameServerPacket
{
	private int boat_id;
	private Location _loc;
	
	public ExStopMoveAirShip(Boat boat)
	{
		this.boat_id = boat.getObjectId();
		this._loc = boat.getLoc();
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x66);
		this.writeD(this.boat_id);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
	}
}