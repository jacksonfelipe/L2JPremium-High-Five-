package npc.model;

import premium.commons.util.Rnd;
import premium.gameserver.Config;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Party;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.RaidBossInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.network.serverpackets.SystemMessage;
import premium.gameserver.templates.npc.NpcTemplate;

public class CannibalisticStakatoChiefInstance extends RaidBossInstance
{
	private static final int ITEMS[] =
	{
		14833,
		14834
	};
	
	public CannibalisticStakatoChiefInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		if (killer == null)
		{
			return;
		}
		Creature topdam = getAggroList().getTopDamager();
		if (topdam == null)
		{
			topdam = killer;
		}
		Player pc = topdam.getPlayer();
		if (pc == null)
		{
			return;
		}
		Party party = pc.getParty();
		int itemId;
		if (party != null)
		{
			for (Player partyMember : party.getMembers())
			{
				if (partyMember != null && pc.isInRange(partyMember, Config.ALT_PARTY_DISTRIBUTION_RANGE))
				{
					itemId = ITEMS[Rnd.get(ITEMS.length)];
					partyMember.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1).addItemName(itemId));
					ItemInstance createdItem = partyMember.getInventory().addItem(itemId, 1, "CannibalisticStakatoChiefInstance");
				}
			}
		}
		else
		{
			itemId = ITEMS[Rnd.get(ITEMS.length)];
			pc.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1).addItemName(itemId));
			ItemInstance createdItem = pc.getInventory().addItem(itemId, 1, "CannibalisticStakatoChiefInstance");
		}
	}
}