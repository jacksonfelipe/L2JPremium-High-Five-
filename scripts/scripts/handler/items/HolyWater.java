package handler.items;

import premium.gameserver.handler.items.ItemHandler;
import premium.gameserver.model.GameObject;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.MagicSkillUse;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.scripts.ScriptFile;
import npc.model.HellboundRemnantInstance;

public class HolyWater extends SimpleItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[]
	{
		9673
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
		
		if (target == null || !(target instanceof HellboundRemnantInstance))
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		
		HellboundRemnantInstance npc = (HellboundRemnantInstance) target;
		if (npc.isDead())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		
		player.broadcastPacket(new MagicSkillUse(player, npc, 2358, 1, 0, 0));
		npc.onUseHolyWater(player);
		
		return true;
	}
}
