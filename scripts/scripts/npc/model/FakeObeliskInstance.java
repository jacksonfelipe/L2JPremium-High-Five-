package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;

 
public class FakeObeliskInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public FakeObeliskInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
	}
	
	@Override
	public void onAction(Player player, boolean shift)
	{
		player.sendActionFailed();
	}
}