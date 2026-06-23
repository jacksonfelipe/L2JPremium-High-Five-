package npc.model;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;
import premium.gameserver.templates.npc.NpcTemplate;

public class SeducedInvestigatorInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public SeducedInvestigatorInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setHasChatWindow(true);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		player.sendPacket(new NpcHtmlMessage(player, this, "common/seducedinvestigator.htm", val));
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		Player player = attacker.getPlayer();
		if ((player == null) || player.isPlayable())
		{
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}
	
	@Override
	public boolean canChampion()
	{
		return false;
	}
}