package premium.gameserver.model.items.listeners;

import premium.gameserver.data.xml.holder.OptionDataHolder;
import premium.gameserver.listener.inventory.OnEquipListener;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.SkillList;
import premium.gameserver.stats.triggers.TriggerInfo;
import premium.gameserver.templates.OptionDataTemplate;

public final class ItemEnchantOptionsListener implements OnEquipListener
{
	private static final ItemEnchantOptionsListener _instance = new ItemEnchantOptionsListener();
	
	public static ItemEnchantOptionsListener getInstance()
	{
		return _instance;
	}
	
	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable())
		{
			return;
		}
		Player player = actor.getPlayer();
		
		boolean needSendInfo = false;
		for (int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if (template == null)
			{
				continue;
			}
			
			player.addStatFuncs(template.getStatFuncs(template));
			for (Skill skill : template.getSkills())
			{
				player.addSkill(skill, false);
				needSendInfo = true;
			}
			for (TriggerInfo triggerInfo : template.getTriggerList())
			{
				player.addTrigger(triggerInfo);
			}
		}
		
		if (needSendInfo)
		{
			player.sendPacket(new SkillList(player));
		}
		player.sendChanges();
	}
	
	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable())
		{
			return;
		}
		
		Player player = actor.getPlayer();
		
		boolean needSendInfo = false;
		for (int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if (template == null)
			{
				continue;
			}
			
			player.removeStatsOwner(template);
			for (Skill skill : template.getSkills())
			{
				player.removeSkill(skill, false);
				needSendInfo = true;
			}
			for (TriggerInfo triggerInfo : template.getTriggerList())
			{
				player.removeTrigger(triggerInfo);
			}
		}
		
		if (needSendInfo)
		{
			player.sendPacket(new SkillList(player));
		}
		player.sendChanges();
	}
}
