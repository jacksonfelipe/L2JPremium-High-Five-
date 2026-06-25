package premium.gameserver.model.entity.events.objects;

import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.ArrayUtils;

import premium.commons.dao.JdbcEntityState;
import premium.commons.threading.RunnableImpl;
import premium.gameserver.Config;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.data.xml.holder.EventHolder;
import premium.gameserver.data.xml.holder.NpcHolder;
import premium.gameserver.idfactory.IdFactory;
import premium.gameserver.model.Creature;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.Zone.ZoneType;
import premium.gameserver.model.entity.events.EventType;
import premium.gameserver.model.entity.events.GlobalEvent;
import premium.gameserver.model.entity.events.impl.DominionSiegeEvent;
import premium.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.model.instances.TerritoryWardInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.model.items.attachment.FlagItemAttachment;
import premium.gameserver.network.serverpackets.SystemMessage2;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.ItemFunctions;
import premium.gameserver.utils.Location;

public class TerritoryWardObject implements SpawnableObject, FlagItemAttachment
{
	private static final long serialVersionUID = 1L;
	private final int _itemId;
	private final NpcTemplate _template;
	private final Location _location;
	private boolean _isOutOfZone;
	private ScheduledFuture<?> _startTimerTask;
	private NpcInstance _wardNpcInstance;
	private ItemInstance _wardItemInstance;
	
	public TerritoryWardObject(int itemId, int npcId, Location location)
	{
		_itemId = itemId;
		_template = NpcHolder.getInstance().getTemplate(npcId);
		_location = location;
	}
	
	@Override
	public void spawnObject(GlobalEvent event)
	{
		_wardItemInstance = ItemFunctions.createItem(_itemId);
		_wardItemInstance.setAttachment(this);
		
		_wardNpcInstance = new TerritoryWardInstance(IdFactory.getInstance().getNextId(), _template, this);
		_wardNpcInstance.addEvent(event);
		_wardNpcInstance.setCurrentHpMp(_wardNpcInstance.getMaxHp(), _wardNpcInstance.getMaxMp());
		_wardNpcInstance.spawnMe(_location);
		_startTimerTask = null;
		_isOutOfZone = false;
	}
	
	private void stopTerritoryFlagCountDown()
	{
		if (_startTimerTask != null)
		{
			_startTimerTask.cancel(false);
			_startTimerTask = null;
			_isOutOfZone = false;
		}
	}
	
	@Override
	public void despawnObject(GlobalEvent event)
	{
		if (_wardItemInstance == null || _wardNpcInstance == null)
		{
			return;
		}
		
		Player owner = GameObjectsStorage.getPlayer(_wardItemInstance.getOwnerId());
		if (owner != null)
		{
			owner.getInventory().destroyItem(_wardItemInstance, "Territory Ward");
			owner.sendDisarmMessage(_wardItemInstance);
		}
		_wardItemInstance.setAttachment(null);
		_wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
		_wardItemInstance.delete();
		_wardItemInstance.deleteMe();
		_wardItemInstance = null;
		
		_wardNpcInstance.deleteMe();
		_wardNpcInstance = null;
		stopTerritoryFlagCountDown();
	}
	
	@Override
	public void refreshObject(GlobalEvent event)
	{
		//
	}
	
	@Override
	public void onLogout(Player player)
	{
		// Infern0 on logout drop the flag on the ground do not return it to castle.
		final Location loc = player.getLoc();
		player.getInventory().removeItem(_wardItemInstance, "Territory Ward");
		
		_wardItemInstance.setOwnerId(0);
		_wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
		_wardItemInstance.update();
		
		_wardNpcInstance.setCurrentHpMp(_wardNpcInstance.getMaxHp(), _wardNpcInstance.getMaxMp(), true);
		_wardNpcInstance.spawnMe(loc);
		stopTerritoryFlagCountDown();
		_isOutOfZone = false;
	}
	
	@Override
	public void onDeath(Player owner, Creature killer)
	{
		final Location loc = owner.getLoc();
		owner.getInventory().removeItem(_wardItemInstance, "Territory Ward");
		owner.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_DROPPED_S1).addName(_wardItemInstance));
		
		_wardItemInstance.setOwnerId(0);
		_wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
		_wardItemInstance.update();
		_wardNpcInstance.setCurrentHpMp(_wardNpcInstance.getMaxHp(), _wardNpcInstance.getMaxMp(), true);
		_wardNpcInstance.spawnMe(loc);
		final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.THE_CHARACTER_THAT_ACQUIRED_S1S_WARD_HAS_BEEN_KILLED).addResidenceName(getDominionId()));
		stopTerritoryFlagCountDown();
		_isOutOfZone = false;
	}
	
	@Override
	public void onOutTerritory(Player player)
	{
		if (!Config.DOMINION_REMOVE_FLAG_ON_LEAVE_ZONE)
		{
			return;
		}
		player.getInventory().removeItem(_wardItemInstance, "Territory Ward");
		_wardItemInstance.setOwnerId(0);
		_wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
		_wardItemInstance.update();
		_wardNpcInstance.setCurrentHpMp(_wardNpcInstance.getMaxHp(), _wardNpcInstance.getMaxMp(), true);
		_wardNpcInstance.spawnMe(_location);
		final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.THE_CHARACTER_THAT_ACQUIRED_S1S_WARD_HAS_BEEN_KILLED).addResidenceName(getDominionId()));
	}
	
	@Override
	public boolean canPickUp(Player player)
	{
		return player.getActiveWeaponFlagAttachment() == null;
	}
	
	@Override
	public void pickUp(Player player)
	{
		player.getInventory().addItem(_wardItemInstance, "Territory Ward");
		player.getInventory().equipItem(_wardItemInstance);
		
		player.sendPacket(SystemMsg.YOUVE_ACQUIRED_THE_WARD);
		
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.THE_S1_WARD_HAS_BEEN_DESTROYED_C2_NOW_HAS_THE_TERRITORY_WARD).addResidenceName(getDominionId()).addName(player));
		checkZoneForFlag(player);
	}
	
	public boolean isFlagOut()
	{
		return _isOutOfZone;
	}
	
	private void checkZoneForFlag(Player player)
	{
		if (!player.isInZone(ZoneType.SIEGE))
		{
			startTerrFlagCountDown(player);
		}
	}
	
	public void startTerrFlagCountDown(Player player)
	{
		if (_startTimerTask != null)
		{
			_startTimerTask.cancel(false);
			_startTimerTask = null;
		}
		_startTimerTask = ThreadPoolManager.getInstance().schedule(new DropFlagInstance(player), Config.INTERVAL_FLAG_DROP * 1000);
		
		player.sendMessage("You've leaved the battle zone! The flag will dissapear in " + Config.INTERVAL_FLAG_DROP + " seconds!");
		
		_isOutOfZone = true;
	}
	
	@Override
	public boolean canAttack(Player player)
	{
		player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
		return false;
	}
	
	@Override
	public boolean canCast(Player player, Skill skill)
	{
		if (player.getActiveWeaponItem() == null)
		{
			player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
			return false;
		}
		final Skill[] skills = player.getActiveWeaponItem().getAttachedSkills();
		if (!ArrayUtils.contains(skills, skill))
		{
			player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean canBeLost()
	{
		return true;
	}
	
	@Override
	public boolean canBeUnEquiped()
	{
		return false;
	}
	
	@Override
	public void setItem(ItemInstance item)
	{
		
	}
	
	public Location getWardLocation()
	{
		if (_wardItemInstance == null || _wardNpcInstance == null)
		{
			return null;
		}
		
		if (_wardItemInstance.getOwnerId() > 0)
		{
			final Player player = GameObjectsStorage.getPlayer(_wardItemInstance.getOwnerId());
			if (player != null)
			{
				return player.getLoc();
			}
		}
		
		return _wardNpcInstance.getLoc();
	}
	
	public NpcInstance getWardNpcInstance()
	{
		return _wardNpcInstance;
	}
	
	public ItemInstance getWardItemInstance()
	{
		return _wardItemInstance;
	}
	
	public int getDominionId()
	{
		return _itemId - 13479;
	}
	
	public DominionSiegeEvent getEvent()
	{
		return _wardNpcInstance.getEvent(DominionSiegeEvent.class);
	}
	
	private class DropFlagInstance extends RunnableImpl
	{
		private final Player _player;
		
		public DropFlagInstance(Player player)
		{
			_player = player;
		}
		
		@Override
		public void runImpl()
		{
			onLogout(_player);
		}
	}
}
