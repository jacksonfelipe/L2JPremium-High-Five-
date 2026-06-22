package handler.items;

import premium.gameserver.cache.Msg;
import premium.gameserver.handler.items.ItemHandler;
import premium.gameserver.instancemanager.MapRegionManager;
import premium.gameserver.model.Manor;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.instances.ChestInstance;
import premium.gameserver.model.instances.MinionInstance;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.model.instances.RaidBossInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.scripts.ScriptFile;
import premium.gameserver.tables.SkillTable;
import premium.gameserver.templates.mapregion.DomainArea;

public class Seed extends ScriptItemHandler implements ScriptFile
{
	private static int[] _itemIds = {};
	
	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}
	
	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}
	
	@Override
	public void onReload()
	{
		
	}
	
	@Override
	public void onShutdown()
	{
		
	}
	
	public Seed()
	{
		_itemIds = new int[Manor.getInstance().getAllSeeds().size()];
		int id = 0;
		for (Integer s : Manor.getInstance().getAllSeeds().keySet())
		{
			_itemIds[id++] = s.shortValue();
		}
	}
	
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;
		
		// Цель не выбрана
		if (playable.getTarget() == null)
		{
			player.sendActionFailed();
			return false;
		}
		
		// Цель не моб, РБ или миньон
		if (!player.getTarget().isMonster() || player.getTarget() instanceof RaidBossInstance || player.getTarget() instanceof MinionInstance && ((MinionInstance) player.getTarget()).getLeader() instanceof RaidBossInstance || player.getTarget() instanceof ChestInstance || ((MonsterInstance) playable.getTarget()).getChampion() > 0 && !item.isAltSeed())
		{
			player.sendPacket(SystemMsg.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return false;
		}
		
		MonsterInstance target = (MonsterInstance) playable.getTarget();
		
		// Моб мертв
		if ((target == null) || target.isDead())
		{
			player.sendPacket(Msg.INVALID_TARGET);
			return false;
		}
		
		// Уже посеяно
		if (target.isSeeded())
		{
			player.sendPacket(SystemMsg.THE_SEED_HAS_BEEN_SOWN);
			return false;
		}
		
		int seedId = item.getItemId();
		if (seedId == 0 || player.getInventory().getItemByItemId(item.getItemId()) == null)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return false;
		}
		
		DomainArea domain = MapRegionManager.getInstance().getRegionData(DomainArea.class, player);
		int castleId = domain == null ? 0 : domain.getId();
		// Несовпадение зоны
		if (Manor.getInstance().getCastleIdForSeed(seedId) != castleId)
		{
			player.sendPacket(SystemMsg.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return false;
		}
		
		// use Sowing skill, id 2097
		Skill skill = SkillTable.getInstance().getInfo(2097, 1);
		if (skill == null)
		{
			player.sendActionFailed();
			return false;
		}
		
		if (skill.checkCondition(player, target, false, false, true))
		{
			player.setUseSeed(seedId);
			player.getAI().Cast(skill, target);
		}
		return true;
	}
	
	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}