package premium.gameserver.model.instances;

import premium.gameserver.Config;
import premium.gameserver.instancemanager.games.FishingChampionShipManager;
import premium.gameserver.model.Player;
import premium.gameserver.templates.npc.NpcTemplate;

public class FishermanInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public FishermanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom = "";
		
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "fisherman/" + pom + ".htm";
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		if (command.equalsIgnoreCase("FishingSkillList"))
		{
			showFishingSkillList(player);
		}
		else if (command.startsWith("FishingChampionship") && Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionShipManager.getInstance().showChampScreen(player, this);
		}
		else if (command.startsWith("FishingReward") && Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionShipManager.getInstance().getReward(player);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}