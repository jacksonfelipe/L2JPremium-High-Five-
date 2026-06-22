package premium.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.List;

import premium.commons.collections.MultiValueSet;
import premium.gameserver.Config;
import premium.gameserver.data.BoatHolder;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;
import premium.gameserver.model.World;
import premium.gameserver.model.entity.boat.Boat;
import premium.gameserver.model.entity.boat.ClanAirShip;
import premium.gameserver.model.entity.events.GlobalEvent;
import premium.gameserver.model.entity.events.objects.BoatPoint;
import premium.gameserver.network.serverpackets.L2GameServerPacket;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.utils.Location;
import premium.gameserver.utils.MapUtils;

public class BoatWayEvent extends GlobalEvent
{
	public static final String BOAT_POINTS = "boat_points";
	
	private final int _ticketId;
	private final Location _returnLoc;
	private final Boat _boat;
	
	public BoatWayEvent(ClanAirShip boat)
	{
		super(boat.getObjectId(), "ClanAirShip");
		_ticketId = 0;
		_boat = boat;
		_returnLoc = null;
	}
	
	public BoatWayEvent(MultiValueSet<String> set)
	{
		super(set);
		_ticketId = set.getInteger("ticketId", 0);
		_returnLoc = Location.parseLoc(set.getString("return_point"));
		String className = set.getString("class", null);
		if (className != null)
		{
			_boat = BoatHolder.getInstance().initBoat(getName(), className);
			Location loc = Location.parseLoc(set.getString("spawn_point"));
			_boat.setLoc(loc, true);
			_boat.setHeading(loc.h);
		}
		else
		{
			_boat = BoatHolder.getInstance().getBoat(getName());
		}
		_boat.setWay(className != null ? 1 : 0, this);
	}
	
	@Override
	public void initEvent()
	{
	}
	
	@Override
	public void startEvent()
	{
		L2GameServerPacket startPacket = _boat.startPacket();
		for (Player player : _boat.getPlayers())
		{
			if ((_ticketId <= 0) || player.consumeItem(_ticketId, 1))
			{
				if (startPacket != null)
				{
					player.sendPacket(startPacket);
				}
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
				_boat.oustPlayer(player, _returnLoc, true);
			}
		}
		
		moveNext();
	}
	
	public void moveNext()
	{
		List<BoatPoint> points = getObjects(BOAT_POINTS);
		
		if (_boat.getRunState() >= points.size())
		{
			_boat.trajetEnded(true);
			return;
		}
		
		final BoatPoint bp = points.get(_boat.getRunState());
		
		if (bp.getSpeed1() >= 0)
		{
			_boat.setMoveSpeed(bp.getSpeed1());
		}
		if (bp.getSpeed2() >= 0)
		{
			_boat.setRotationSpeed(bp.getSpeed2());
		}
		
		if (_boat.getRunState() == 0)
		{
			_boat.broadcastCharInfo();
		}
		
		_boat.setRunState(_boat.getRunState() + 1);
		
		if (bp.isTeleport())
		{
			_boat.teleportShip(bp.getX(), bp.getY(), bp.getZ());
		}
		else
		{
			_boat.moveToLocation(bp.getX(), bp.getY(), bp.getZ(), 0, false);
		}
	}
	
	@Override
	public void reCalcNextTime(boolean onInit)
	{
		registerActions();
	}
	
	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis();
	}
	
	@Override
	public List<Player> broadcastPlayers(int range)
	{
		if (range <= 0)
		{
			List<Player> list = new ArrayList<Player>();
			
			int rx = MapUtils.regionX(_boat.getX());
			int ry = MapUtils.regionY(_boat.getY());
			int offset = Config.SHOUT_OFFSET;
			
			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (player.getReflection() != _boat.getReflection())
				{
					continue;
				}
				
				int tx = MapUtils.regionX(player);
				int ty = MapUtils.regionY(player);
				
				if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
				{
					list.add(player);
				}
			}
			
			return list;
		}
		else
		{
			return World.getAroundPlayers(_boat, range, Math.max(range / 2, 200));
		}
	}
	
	@Override
	protected void printInfo()
	{
	}
	
	public Location getReturnLoc()
	{
		return _returnLoc;
	}
}
