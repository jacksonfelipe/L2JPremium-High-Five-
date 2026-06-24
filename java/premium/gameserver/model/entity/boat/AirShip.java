package premium.gameserver.model.entity.boat;

import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.ExAirShipInfo;
import premium.gameserver.network.serverpackets.ExGetOffAirShip;
import premium.gameserver.network.serverpackets.ExGetOnAirShip;
import premium.gameserver.network.serverpackets.ExMoveToLocationAirShip;
import premium.gameserver.network.serverpackets.ExMoveToLocationInAirShip;
import premium.gameserver.network.serverpackets.ExStopMoveAirShip;
import premium.gameserver.network.serverpackets.ExStopMoveInAirShip;
import premium.gameserver.network.serverpackets.ExValidateLocationInAirShip;
import premium.gameserver.network.serverpackets.L2GameServerPacket;
import premium.gameserver.templates.CharTemplate;
import premium.gameserver.utils.Location;

public class AirShip extends Boat
{
	private static final long serialVersionUID = 1L;

	public AirShip(int objectId, CharTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public L2GameServerPacket infoPacket()
	{
		return new ExAirShipInfo(this);
	}
	
	@Override
	public L2GameServerPacket movePacket()
	{
		return new ExMoveToLocationAirShip(this);
	}
	
	@Override
	public L2GameServerPacket inMovePacket(Player player, Location src, Location desc)
	{
		return new ExMoveToLocationInAirShip(player, this, src, desc);
	}
	
	@Override
	public L2GameServerPacket stopMovePacket()
	{
		return new ExStopMoveAirShip(this);
	}
	
	@Override
	public L2GameServerPacket inStopMovePacket(Player player)
	{
		return new ExStopMoveInAirShip(player);
	}
	
	@Override
	public L2GameServerPacket startPacket()
	{
		return null;
	}
	
	@Override
	public L2GameServerPacket checkLocationPacket()
	{
		return null;
	}
	
	@Override
	public L2GameServerPacket validateLocationPacket(Player player)
	{
		return new ExValidateLocationInAirShip(player);
	}
	
	@Override
	public L2GameServerPacket getOnPacket(Player player, Location location)
	{
		return new ExGetOnAirShip(player, this, location);
	}
	
	@Override
	public L2GameServerPacket getOffPacket(Player player, Location location)
	{
		return new ExGetOffAirShip(player, this, location);
	}
	
	@Override
	public boolean isAirShip()
	{
		return true;
	}
	
	@Override
	public void oustPlayers()
	{
		for (Player player : _players)
		{
			oustPlayer(player, getReturnLoc(), true);
		}
	}
}
