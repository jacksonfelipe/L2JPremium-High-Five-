package l2mv.gameserver.model.instances;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class NoActionNpcInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;
	
	public NoActionNpcInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}
	
	@Override
	public void onAction(Player player, boolean dontMove)
	{
		player.sendActionFailed();
	}
}
