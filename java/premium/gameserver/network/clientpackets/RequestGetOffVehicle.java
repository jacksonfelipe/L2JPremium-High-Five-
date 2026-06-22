package premium.gameserver.network.clientpackets;

import premium.gameserver.data.BoatHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.boat.Boat;
import premium.gameserver.utils.Location;

public class RequestGetOffVehicle extends L2GameClientPacket
{
	// Format: cdddd
	private int _objectId;
	private Location _location = new Location();
	
	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this._location.x = this.readD();
		this._location.y = this.readD();
		this._location.z = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		Boat boat = BoatHolder.getInstance().getBoat(this._objectId);
		if (boat == null || boat.isMoving)
		{
			player.sendActionFailed();
			return;
		}
		
		boat.oustPlayer(player, this._location, false);
	}
}