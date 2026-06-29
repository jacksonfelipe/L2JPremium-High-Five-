package premium.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import premium.commons.geometry.Rectangle;
import premium.commons.util.Rnd;
import premium.gameserver.Config;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.SimpleSpawner;
import premium.gameserver.model.Territory;
import premium.gameserver.model.entity.DimensionalRift;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;
import premium.gameserver.network.serverpackets.TeleportToLocation;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.Location;

import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

public class DimensionalRiftManager extends DocumentParser
{
	private static final Logger LOG = LoggerFactory.getLogger(DimensionalRiftManager.class);
	private static DimensionalRiftManager _instance;
	private Map<Integer, Map<Integer, DimensionalRiftRoom>> _rooms = new ConcurrentHashMap<>();
	private final static int DIMENSIONAL_FRAGMENT_ITEM_ID = 7079;
	
	public static DimensionalRiftManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new DimensionalRiftManager();
		}
		
		return _instance;
	}
	
	public DimensionalRiftManager()
	{
		load();
	}
	
	public DimensionalRiftRoom getRoom(int type, int room)
	{
		return _rooms.get(type).get(room);
	}
	
	public Map<Integer, DimensionalRiftRoom> getRooms(int type)
	{
		return _rooms.get(type);
	}
	
	private int _countGood = 0;
	private int _countBad = 0;
	
	@Override
	public void load()
	{
		_countGood = 0;
		_countBad = 0;
		
		parseDatapackFile("data/dimensional_rift.xml");
		
		int typeSize = _rooms.keySet().size();
		int roomSize = 0;
		
		for (int b : _rooms.keySet())
		{
			roomSize += _rooms.get(b).keySet().size();
		}
		
		LOG.info("DimensionalRiftManager: Loaded " + typeSize + " room types with " + roomSize + " rooms.");
		LOG.info("DimensionalRiftManager: Loaded " + _countGood + " DimensionalRift spawns, " + _countBad + " errors.");
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		forEach(doc, "rift", rift ->
		{
			forEach(rift, "area", area ->
			{
				StatsSet areaSet = parseAttributes(area);
				int type = areaSet.getInteger("type");
				
				forEach(area, "room", room ->
				{
					StatsSet roomSet = parseAttributes(room);
					int roomId = roomSet.getInteger("id");
					boolean isBossRoom = roomSet.getBool("isBossRoom", false);
					
					final Territory[] territory = { null };
					final Location[] tele = { new Location() };
					
					forEach(room, coord ->
					{
						if ("teleport".equalsIgnoreCase(coord.getNodeName()))
						{
							StatsSet teleSet = parseAttributes(coord);
							tele[0] = Location.parseLoc(teleSet.getString("loc"));
						}
						else if ("zone".equalsIgnoreCase(coord.getNodeName()))
						{
							StatsSet zoneSet = parseAttributes(coord);
							int xMin = zoneSet.getInteger("xMin");
							int xMax = zoneSet.getInteger("xMax");
							int yMin = zoneSet.getInteger("yMin");
							int yMax = zoneSet.getInteger("yMax");
							int zMin = zoneSet.getInteger("zMin");
							int zMax = zoneSet.getInteger("zMax");
							
							territory[0] = new Territory().add(new Rectangle(xMin, yMin, xMax, yMax).setZmin(zMin).setZmax(zMax));
						}
					});
					
					if (territory[0] == null)
					{
						LOG.error("DimensionalRiftManager: invalid spawn data for room id " + roomId + "!");
					}
					
					if (!_rooms.containsKey(type))
					{
						_rooms.put(type, new ConcurrentHashMap<>());
					}
					
					_rooms.get(type).put(roomId, new DimensionalRiftRoom(territory[0], tele[0], isBossRoom));
					
					forEach(room, "spawn", spawn ->
					{
						StatsSet spawnSet = parseAttributes(spawn);
						int mobId = spawnSet.getInteger("mobId");
						int delay = spawnSet.getInteger("delay");
						int count = spawnSet.getInteger("count");
						
						NpcTemplate template = NpcHolder.getInstance().getTemplate(mobId);
						if (template == null)
						{
							LOG.warn("Template " + mobId + " not found!");
						}
						if (!_rooms.containsKey(type))
						{
							LOG.warn("Type " + type + " not found!");
						}
						else if (!_rooms.get(type).containsKey(roomId))
						{
							LOG.warn("Room " + roomId + " in Type " + type + " not found!");
						}
						
						if (template != null && _rooms.containsKey(type) && _rooms.get(type).containsKey(roomId))
						{
							SimpleSpawner spawnDat = new SimpleSpawner(template);
							spawnDat.setTerritory(territory[0]);
							spawnDat.setHeading(-1);
							spawnDat.setRespawnDelay(delay);
							spawnDat.setAmount(count);
							_rooms.get(type).get(roomId).getSpawns().add(spawnDat);
							_countGood++;
						}
						else
						{
							_countBad++;
						}
					});
				});
			});
		});
	}
	
	public void reload()
	{
		for (int b : _rooms.keySet())
		{
			_rooms.get(b).clear();
		}
		
		_rooms.clear();
		load();
	}
	
	public boolean checkIfInRiftZone(Location loc, boolean ignorePeaceZone)
	{
		if (ignorePeaceZone)
		{
			return _rooms.get(0).get(1).checkIfInZone(loc);
		}
		return _rooms.get(0).get(1).checkIfInZone(loc) && !_rooms.get(0).get(0).checkIfInZone(loc);
	}
	
	public boolean checkIfInPeaceZone(Location loc)
	{
		return _rooms.get(0).get(0).checkIfInZone(loc);
	}
	
	public void teleportToWaitingRoom(Player player)
	{
		teleToLocation(player, Location.findPointToStay(getRoom(0, 0).getTeleportCoords(), 0, 250, ReflectionManager.DEFAULT.getGeoIndex()), null);
	}
	
	public void start(Player player, int type, NpcInstance npc)
	{
		if (!player.isInParty())
		{
			showHtmlFile(player, "rift/NoParty.htm", npc);
			return;
		}
		
		if (!player.isGM())
		{
			if (!player.getParty().isLeader(player))
			{
				showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
				return;
			}
			
			if (player.getParty().isInDimensionalRift())
			{
				showHtmlFile(player, "rift/Cheater.htm", npc);
				
				if (!player.isGM())
				{
					LOG.warn("Player " + player.getName() + "(" + player.getObjectId() + ") was cheating in dimension rift area!");
				}
				
				return;
			}
			
			if (player.getParty().size() < Config.RIFT_MIN_PARTY_SIZE)
			{
				showHtmlFile(player, "rift/SmallParty.htm", npc);
				return;
			}
			
			for (Player p : player.getParty().getMembers())
			{
				if (!checkIfInPeaceZone(p.getLoc()))
				{
					showHtmlFile(player, "rift/NotInWaitingRoom.htm", npc);
					return;
				}
			}
			
			ItemInstance i;
			for (Player p : player.getParty().getMembers())
			{
				i = p.getInventory().getItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID);
				if (i == null || i.getCount() < getNeededItems(type))
				{
					showHtmlFile(player, "rift/NoFragments.htm", npc);
					return;
				}
			}
			
			for (Player p : player.getParty().getMembers())
			{
				if (!p.getInventory().destroyItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID, getNeededItems(type), "DimensionalRift"))
				{
					showHtmlFile(player, "rift/NoFragments.htm", npc);
					return;
				}
			}
		}
		
		new DimensionalRift(player.getParty(), type, Rnd.get(1, _rooms.get(type).size() - 1));
	}
	
	public class DimensionalRiftRoom
	{
		private final Territory _territory;
		private final Location _teleportCoords;
		private final boolean _isBossRoom;
		private final List<SimpleSpawner> _roomSpawns;
		
		public DimensionalRiftRoom(Territory territory, Location tele, boolean isBossRoom)
		{
			_territory = territory;
			_teleportCoords = tele;
			_isBossRoom = isBossRoom;
			_roomSpawns = new ArrayList<>();
		}
		
		public Location getTeleportCoords()
		{
			return _teleportCoords;
		}
		
		public boolean checkIfInZone(Location loc)
		{
			return checkIfInZone(loc.x, loc.y, loc.z);
		}
		
		public boolean checkIfInZone(int x, int y, int z)
		{
			return _territory.isInside(x, y, z);
		}
		
		public boolean isBossRoom()
		{
			return _isBossRoom;
		}
		
		public List<SimpleSpawner> getSpawns()
		{
			return _roomSpawns;
		}
	}
	
	public long getNeededItems(int type)
	{
		switch (type)
		{
			case 1:
				return Config.RIFT_ENTER_COST_RECRUIT;
			case 2:
				return Config.RIFT_ENTER_COST_SOLDIER;
			case 3:
				return Config.RIFT_ENTER_COST_OFFICER;
			case 4:
				return Config.RIFT_ENTER_COST_CAPTAIN;
			case 5:
				return Config.RIFT_ENTER_COST_COMMANDER;
			case 6:
				return Config.RIFT_ENTER_COST_HERO;
			default:
				return Long.MAX_VALUE;
		}
	}
	
	public void showHtmlFile(Player player, String file, NpcInstance npc)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
		html.setFile(file);
		html.replace("%t_name%", npc.getName());
		player.sendPacket(html);
	}
	
	public static void teleToLocation(Player player, Location loc, Reflection ref)
	{
		if (player.isTeleporting() || player.isDeleted())
		{
			return;
		}
		player.setIsTeleporting(true);
		
		player.setTarget(null);
		player.stopMove();
		
		if (player.isInBoat())
		{
			player.setBoat(null);
		}
		
		player.breakFakeDeath();
		
		player.decayMe();
		
		player.setLoc(loc);
		
		if (ref == null)
		{
			player.setReflection(ReflectionManager.DEFAULT);
		}
		
		// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
		player.setLastClientPosition(null);
		player.setLastServerPosition(null);
		player.sendPacket(new TeleportToLocation(player, loc));
	}
}