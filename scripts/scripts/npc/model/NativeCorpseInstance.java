package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public final class NativeCorpseInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public NativeCorpseInstance(int objectId, NpcTemplate template)
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
	public void onRandomAnimation()
	{
		return;
	}
}