package npc.model.residences.clanhall;

import premium.gameserver.model.Creature;
import premium.gameserver.network.serverpackets.components.NpcString;
import premium.gameserver.scripts.Functions;
import premium.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 17:50/13.05.2011
 */
public abstract class _34BossMinionInstance extends SiegeGuardInstance implements _34SiegeGuard
{
	public _34BossMinionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onDeath(Creature killer)
	{
		setCurrentHp(1, true);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		Functions.npcShout(this, spawnChatSay());
	}
	
	public abstract NpcString spawnChatSay();
	
	@Override
	public abstract NpcString teleChatSay();
}
