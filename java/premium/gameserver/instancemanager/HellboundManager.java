package premium.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import premium.commons.geometry.Polygon;
import premium.commons.threading.RunnableImpl;
import premium.gameserver.Config;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.listener.actor.OnDeathListener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.SimpleSpawner;
import premium.gameserver.model.Territory;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.Location;
import premium.gameserver.utils.ReflectionUtils;

import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

/**
 * Manages 11 stages of Hellbound Island and all it's events
 * @author pchayka
 */

public class HellboundManager extends DocumentParser
{
	private static final Logger _log = LoggerFactory.getLogger(HellboundManager.class);
	private static ArrayList<HellboundSpawn> _list;
	private static List<SimpleSpawner> _spawnList;
	private static HellboundManager _instance;
	private static int _initialStage;
	private static final long _taskDelay = 2 * 60 * 1000L; // 30min
	DeathListener _deathListener = new DeathListener();
	
	public static HellboundManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new HellboundManager();
		}
		return _instance;
	}
	
	public HellboundManager()
	{
		load();
		spawnHellbound();
		doorHandler();
		_initialStage = getHellboundLevel();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new StageCheckTask(), _taskDelay, _taskDelay);
		_log.info("Hellbound Manager: Loaded");
	}
	
	public static long getConfidence()
	{
		return ServerVariables.getLong("HellboundConfidence", 0);
	}
	
	public static void addConfidence(long value)
	{
		ServerVariables.set("HellboundConfidence", Math.round(getConfidence() + value * Config.RATE_HELLBOUND_CONFIDENCE));
	}
	
	public static void reduceConfidence(long value)
	{
		long i = getConfidence() - value;
		if (i < 1)
		{
			i = 1;
		}
		ServerVariables.set("HellboundConfidence", i);
	}
	
	public static void setConfidence(long value)
	{
		ServerVariables.set("HellboundConfidence", value);
	}
	
	public static int getHellboundLevel()
	{
		if (Config.HELLBOUND_LEVEL <= getHellboundLevelS())
		{
			return getHellboundLevelS();
		}
		return Config.HELLBOUND_LEVEL;
	}
	
	public static int getHellboundLevelS()
	{
		long confidence = ServerVariables.getLong("HellboundConfidence", 0);
		boolean judesBoxes = ServerVariables.getBool("HB_judesBoxes", false);
		boolean bernardBoxes = ServerVariables.getBool("HB_bernardBoxes", false);
		boolean derekKilled = ServerVariables.getBool("HB_derekKilled", false);
		boolean captainKilled = ServerVariables.getBool("HB_captainKilled", false);
		
		if (confidence < 1)
		{
			return 0;
		}
		else if (confidence >= 1 && confidence < 300000)
		{
			return 1;
		}
		else if (confidence >= 300000 && confidence < 600000)
		{
			return 2;
		}
		else if (confidence >= 600000 && confidence < 1000000)
		{
			return 3;
		}
		else if (confidence >= 1000000 && confidence < 1200000)
		{
			if (derekKilled && judesBoxes && bernardBoxes)
			{
				return 5;
			}
			else if (!derekKilled && judesBoxes && bernardBoxes)
			{
				return 4;
			}
			else if (!derekKilled && (!judesBoxes || !bernardBoxes))
			{
				return 3;
			}
		}
		else if (confidence >= 1200000 && confidence < 1500000)
		{
			return 6;
		}
		else if (confidence >= 1500000 && confidence < 1800000)
		{
			return 7;
		}
		else if (confidence >= 1800000 && confidence < 2100000)
		{
			if (captainKilled)
			{
				return 9;
			}
			return 8;
		}
		else if (confidence >= 2100000 && confidence < 2200000)
		{
			return 10;
		}
		else if (confidence >= 2200000)
		{
			return 11;
		}
		
		return 0;
	}
	
	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature cha, Creature killer)
		{
			if (killer == null || !cha.isMonster() || !killer.isPlayable())
			{
				return;
			}
			
			switch (getHellboundLevel())
			{
				case 0:
					break;
				case 1:
				{
					switch (cha.getNpcId())
					{
						case 22320: // Junior Watchman
						case 22321: // Junior Summoner
						case 22324: // Blind Huntsman
						case 22325: // Blind Watchman
							addConfidence(1);
							break;
						case 22327: // Arcane Scout
						case 22328: // Arcane Guardian
						case 22329: // Arcane Watchman
							addConfidence(3); // confirmed
							break;
						case 22322: // Subjugated Native
						case 22323: // Charmed Native
						case 32299: // Quarry Slave
							reduceConfidence(10);
							break;
					}
					break;
				}
				case 2:
				{
					switch (cha.getNpcId())
					{
						case 18463: // Remnant Diabolist
						case 18464: // Remnant Diviner
							addConfidence(5);
							break;
						case 22322: // Subjugated Native
						case 22323: // Charmed Native
						case 32299: // Quarry Slave
							reduceConfidence(10);
							break;
					}
					break;
				}
				case 3:
				{
					switch (cha.getNpcId())
					{
						case 22342: // Darion's Enforcer
						case 22343: // Darion's Executioner
							addConfidence(3);
							break;
						case 22341: // Keltas
							addConfidence(100);
							break;
						case 22322: // Subjugated Native
						case 22323: // Charmed Native
						case 32299: // Quarry Slave
							reduceConfidence(10);
							break;
					}
					break;
				}
				case 4:
				{
					switch (cha.getNpcId())
					{
						case 18465: // Derek
							addConfidence(10000);
							ServerVariables.set("HB_derekKilled", true);
							break;
						case 22322: // Subjugated Native
						case 22323: // Charmed Native
						case 32299: // Quarry Slave
							reduceConfidence(10);
							break;
					}
					break;
				}
				case 5:
				{
					switch (cha.getNpcId())
					{
						case 22448: // Leodas
							reduceConfidence(50);
							break;
					}
					break;
				}
				case 6:
				{
					switch (cha.getNpcId())
					{
						case 22326: // Hellinark
							addConfidence(500);
							break;
						case 18484: // Naia Failan
							addConfidence(5);
							break;
					}
					break;
				}
				case 8:
				{
					switch (cha.getNpcId())
					{
						case 18466: // Outpost Captain
							addConfidence(10000);
							ServerVariables.set("HB_captainKilled", true);
							break;
					}
					break;
				}
				default:
					break;
			}
		}
	}
	
	private void spawnHellbound()
	{
		SimpleSpawner spawnDat;
		NpcTemplate template;
		
		for (HellboundSpawn hbsi : _list)
		{
			if (ArrayUtils.contains(hbsi.getStages(), getHellboundLevel()))
			{
				try
				{
					template = NpcHolder.getInstance().getTemplate(hbsi.getNpcId());
					for (int i = 0; i < hbsi.getAmount(); i++)
					{
						spawnDat = new SimpleSpawner(template);
						spawnDat.setAmount(1);
						if (hbsi.getLoc() != null)
						{
							spawnDat.setLoc(hbsi.getLoc());
						}
						if (hbsi.getSpawnTerritory() != null)
						{
							spawnDat.setTerritory(hbsi.getSpawnTerritory());
						}
						spawnDat.setReflection(ReflectionManager.DEFAULT);
						spawnDat.setRespawnDelay(hbsi.getRespawn(), hbsi.getRespawnRnd());
						spawnDat.setRespawnTime(0);
						spawnDat.doSpawn(true);
						spawnDat.getLastSpawn().addListener(_deathListener);
						spawnDat.startRespawn();
						_spawnList.add(spawnDat);
					}
				}
				catch (RuntimeException e)
				{
					_log.error("Error while Spawning Hellbound! ", e);
				}
			}
		}
		_log.info("HellboundManager: Spawned " + _spawnList.size() + " mobs and NPCs according to the current Hellbound stage");
	}
	
	@Override
	public void load()
	{
		_list = new ArrayList<>();
		_spawnList = new ArrayList<>();
		
		parseDatapackFile("data/hellbound_spawnlist.xml");
		
		_log.info("HellboundManager: Loaded " + _list.size() + " spawn entries.");
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "data", d1 ->
			{
				StatsSet set = parseAttributes(d1);
				int npcId = set.getInteger("npc_id");
				Location spawnLoc = set.isSet("loc") ? Location.parseLoc(set.getString("loc")) : null;
				int count = set.getInteger("count", 1);
				int respawn = set.getInteger("respawn", 60);
				int respawnRnd = set.getInteger("respawn_rnd", 0);
				
				StringTokenizer st = new StringTokenizer(set.getString("stage"), ";");
				int tokenCount = st.countTokens();
				int[] stages = new int[tokenCount];
				for (int i = 0; i < tokenCount; i++)
				{
					stages[i] = Integer.decode(st.nextToken().trim());
				}
				
				final Territory[] territory = { null };
				forEach(d1, "territory", s1 ->
				{
					Polygon poly = new Polygon();
					forEach(s1, "add", s2 ->
					{
						StatsSet s2Set = parseAttributes(s2);
						int x = s2Set.getInteger("x");
						int y = s2Set.getInteger("y");
						int minZ = s2Set.getInteger("zmin");
						int maxZ = s2Set.getInteger("zmax");
						poly.add(x, y).setZmin(minZ).setZmax(maxZ);
					});
					
					territory[0] = new Territory().add(poly);
					
					if (!poly.validate())
					{
						_log.error("HellboundManager: Invalid spawn territory : " + poly + '!');
						territory[0] = null;
					}
				});
				
				if (spawnLoc == null && territory[0] == null)
				{
					_log.error("HellboundManager: no spawn data for npc id : " + npcId + '!');
					return;
				}
				
				HellboundSpawn hbs = new HellboundSpawn(npcId, spawnLoc, count, territory[0], respawn, respawnRnd, stages);
				_list.add(hbs);
			});
		});
	}
	
	public void despawnHellbound()
	{
		for (SimpleSpawner spawnToDelete : _spawnList)
		{
			spawnToDelete.deleteAll();
		}
		
		_spawnList.clear();
	}
	
	private class StageCheckTask extends RunnableImpl
	{
		@Override
		public void runImpl()  
		{
			if (_initialStage != getHellboundLevel())
			{
				despawnHellbound();
				spawnHellbound();
				doorHandler();
				_initialStage = getHellboundLevel();
			}
		}
	}
	
	public class HellboundSpawn
	{
		private final int _npcId;
		private final Location _loc;
		private final int _amount;
		private final Territory _spawnTerritory;
		private final int _respawn;
		private final int _respawnRnd;
		private final int[] _stages;
		
		public HellboundSpawn(int npcId, Location loc, int amount, Territory spawnTerritory, int respawn, int respawnRnd, int[] stages)
		{
			_npcId = npcId;
			_loc = loc;
			_amount = amount;
			_spawnTerritory = spawnTerritory;
			_respawn = respawn;
			_respawnRnd = respawnRnd;
			_stages = stages;
		}
		
		public int getNpcId()
		{
			return _npcId;
		}
		
		public Location getLoc()
		{
			return _loc;
		}
		
		public int getAmount()
		{
			return _amount;
		}
		
		public Territory getSpawnTerritory()
		{
			return _spawnTerritory;
		}
		
		public int getRespawn()
		{
			return _respawn;
		}
		
		public int getRespawnRnd()
		{
			return _respawnRnd;
		}
		
		public int[] getStages()
		{
			return _stages;
		}
	}
	
	private static void doorHandler()
	{
		final int NativeHell_native0131 = 19250001; // Kief room
		final int NativeHell_native0132 = 19250002;
		final int NativeHell_native0133 = 19250003; // Another room
		final int NativeHell_native0134 = 19250004;
		
		final int sdoor_trans_mesh00 = 20250002;
		final int Hell_gate_door = 20250001;
		
		final int[] _doors =
		{
			NativeHell_native0131,
			NativeHell_native0132,
			NativeHell_native0133,
			NativeHell_native0134,
			sdoor_trans_mesh00,
			Hell_gate_door
		};
		
		for (int i = 0; i < _doors.length; i++)
		{
			ReflectionUtils.getDoor(_doors[i]).closeMe();
		}
		
		switch (getHellboundLevel())
		{
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				ReflectionUtils.getDoor(NativeHell_native0131).openMe();
				ReflectionUtils.getDoor(NativeHell_native0132).openMe();
				break;
			case 6:
				ReflectionUtils.getDoor(NativeHell_native0131).openMe();
				ReflectionUtils.getDoor(NativeHell_native0132).openMe();
				break;
			case 7:
				ReflectionUtils.getDoor(NativeHell_native0131).openMe();
				ReflectionUtils.getDoor(NativeHell_native0132).openMe();
				ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
				break;
			case 8:
				ReflectionUtils.getDoor(NativeHell_native0131).openMe();
				ReflectionUtils.getDoor(NativeHell_native0132).openMe();
				ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
				break;
			case 9:
				ReflectionUtils.getDoor(NativeHell_native0131).openMe();
				ReflectionUtils.getDoor(NativeHell_native0132).openMe();
				ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
				ReflectionUtils.getDoor(Hell_gate_door).openMe();
				break;
			case 10:
				ReflectionUtils.getDoor(NativeHell_native0131).openMe();
				ReflectionUtils.getDoor(NativeHell_native0132).openMe();
				ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
				ReflectionUtils.getDoor(Hell_gate_door).openMe();
				break;
			case 11:
				ReflectionUtils.getDoor(NativeHell_native0131).openMe();
				ReflectionUtils.getDoor(NativeHell_native0132).openMe();
				ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
				ReflectionUtils.getDoor(Hell_gate_door).openMe();
				break;
			default:
				break;
		}
	}
}