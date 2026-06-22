package premium.gameserver.model.instances;

import premium.gameserver.model.Player;
import premium.gameserver.templates.npc.NpcTemplate;

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
