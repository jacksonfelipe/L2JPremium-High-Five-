package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;

public final class GuradsOfDawnInstance extends NpcInstance
{
	/**
	 * Quest _195_SevenSignsSecretRitualofthePriests
	 */
	private static final long serialVersionUID = -5723706476543757479L;
	
	public GuradsOfDawnInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		//
	}
	
	@Override
	public void showChatWindow(Player player, String filename, Object... replace)
	{
		//
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		//
	}
}