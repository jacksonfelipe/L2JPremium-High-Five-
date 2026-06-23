package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.MonsterInstance;
import premium.gameserver.templates.npc.NpcTemplate;

public final class GvGBossInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public GvGBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
	}
	
	@Override
	public void showChatWindow(Player player, String filename, Object... replace)
	{
	}
	
	@Override
	public boolean canChampion()
	{
		return false;
	}
	
	@Override
	public boolean isFearImmune()
	{
		return true;
	}
	
	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}
	
	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}