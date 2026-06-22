package premium.gameserver.network.clientpackets;

import premium.gameserver.data.BoatHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.boat.Boat;
import premium.gameserver.utils.Location;

public class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private Location _pos = new Location();
	private Location _originPos = new Location();
	private int _boatObjectId;
	
	@Override
	protected void readImpl()
	{
		this._boatObjectId = this.readD();
		this._pos.x = this.readD();
		this._pos.y = this.readD();
		this._pos.z = this.readD();
		this._originPos.x = this.readD();
		this._originPos.y = this.readD();
		this._originPos.z = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		player.isntAfk();
		
		Boat boat = BoatHolder.getInstance().getBoat(this._boatObjectId);
		if (boat == null)
		{
			player.sendActionFailed();
			return;
		}
		
		boat.moveInBoat(player, this._originPos, this._pos);
	}
}