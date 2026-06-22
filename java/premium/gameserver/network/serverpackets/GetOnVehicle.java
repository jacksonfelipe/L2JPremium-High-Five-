package premium.gameserver.network.serverpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.entity.boat.Boat;
import premium.gameserver.utils.Location;

public class GetOnVehicle extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;
	
	public GetOnVehicle(Player activeChar, Boat boat, Location loc)
	{
		this._loc = loc;
		this._playerObjectId = activeChar.getObjectId();
		this._boatObjectId = boat.getObjectId();
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeC(0x6e);
		this.writeD(this._playerObjectId);
		this.writeD(this._boatObjectId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
	}
}