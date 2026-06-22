package premium.gameserver.network.serverpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.entity.boat.Boat;
import premium.gameserver.utils.Location;

public class ExGetOnAirShip extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;
	
	public ExGetOnAirShip(Player cha, Boat boat, Location loc)
	{
		this._playerObjectId = cha.getObjectId();
		this._boatObjectId = boat.getObjectId();
		this._loc = loc;
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x63);
		this.writeD(this._playerObjectId);
		this.writeD(this._boatObjectId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
	}
}