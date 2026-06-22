package handler.items;

import premium.gameserver.handler.items.ItemHandler;
import premium.gameserver.model.GameObject;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.scripts.ScriptFile;
import premium.gameserver.tables.SkillTable;

public class Harvester extends SimpleItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[]
	{
		5125
	};
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
	
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
	
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		GameObject target = player.getTarget();
		if (target == null || !target.isMonster())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		
		MonsterInstance monster = (MonsterInstance) player.getTarget();
		
		if (!monster.isDead())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		
		Skill skill = SkillTable.getInstance().getInfo(2098, 1);
		if (skill != null && skill.checkCondition(player, monster, false, false, true))
		{
			player.getAI().Cast(skill, monster);
			return true;
		}
		
		return false;
	}
}
