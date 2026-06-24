package premium.gameserver.model.entity.boat;

import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.GetOffVehicle;
import premium.gameserver.network.serverpackets.GetOnVehicle;
import premium.gameserver.network.serverpackets.L2GameServerPacket;
import premium.gameserver.network.serverpackets.MoveToLocationInVehicle;
import premium.gameserver.network.serverpackets.StopMove;
import premium.gameserver.network.serverpackets.StopMoveToLocationInVehicle;
import premium.gameserver.network.serverpackets.ValidateLocationInVehicle;
import premium.gameserver.network.serverpackets.VehicleCheckLocation;
import premium.gameserver.network.serverpackets.VehicleDeparture;
import premium.gameserver.network.serverpackets.VehicleInfo;
import premium.gameserver.network.serverpackets.VehicleStart;
import premium.gameserver.templates.CharTemplate;
import premium.gameserver.utils.Location;

public class Vehicle extends Boat
{
	private static final long serialVersionUID = 1L;

	public Vehicle(int objectId, CharTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public L2GameServerPacket startPacket()
	{
		return new VehicleStart(this);
	}
	
	@Override
	public L2GameServerPacket validateLocationPacket(Player player)
	{
		return new ValidateLocationInVehicle(player);
	}
	
	@Override
	public L2GameServerPacket checkLocationPacket()
	{
		return new VehicleCheckLocation(this);
	}
	
	@Override
	public L2GameServerPacket infoPacket()
	{
		return new VehicleInfo(this);
	}
	
	@Override
	public L2GameServerPacket movePacket()
	{
		return new VehicleDeparture(this);
	}
	
	@Override
	public L2GameServerPacket inMovePacket(Player player, Location src, Location desc)
	{
		return new MoveToLocationInVehicle(player, this, src, desc);
	}
	
	@Override
	public L2GameServerPacket stopMovePacket()
	{
		return new StopMove(this);
	}
	
	@Override
	public L2GameServerPacket inStopMovePacket(Player player)
	{
		return new StopMoveToLocationInVehicle(player);
	}
	
	@Override
	public L2GameServerPacket getOnPacket(Player player, Location location)
	{
		return new GetOnVehicle(player, this, location);
	}
	
	@Override
	public L2GameServerPacket getOffPacket(Player player, Location location)
	{
		return new GetOffVehicle(player, this, location);
	}
	
	@Override
	public void oustPlayers()
	{
		//
	}
	
	@Override
	public boolean isVehicle()
	{
		return true;
	}
}
