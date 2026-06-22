package premium.gameserver.model.entity.tournament.permission;

import org.apache.commons.lang3.ArrayUtils;

import premium.gameserver.ConfigHolder;
import premium.gameserver.model.Playable;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.tournament.BattleInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.actor.player.UseItemPermission;

public class TournamentUseItemPermission implements UseItemPermission
{
	private final BattleInstance battle;
	
	public TournamentUseItemPermission(BattleInstance battle)
	{
		super();
		this.battle = battle;
	}
	
	@Override
	public boolean canUseItem(Playable actor, ItemInstance item, boolean ctrlPressed)
	{
		return canUseItem(battle, actor.getPlayer(), item);
	}
	
	@Override
	public void sendPermissionDeniedError(Playable actor, ItemInstance item, boolean ctrlPressed)
	{
		actor.getPlayer().sendCustomMessage("Tournament.NotAllowed.UseItem", new Object[0]);
	}
	
	public static boolean canUseItem(BattleInstance battle, Player player, ItemInstance item)
	{
		return ArrayUtils.contains(ConfigHolder.getIntArray("TournamentOtherItemsAllowedToUse"), item.getItemId()) || ConfigHolder.getMap("TournamentItemsToEveryPlayer", Integer.class, Long.class).containsKey(item.getItemId()) && (item.isStackable() || battle.containsReceivedItem(player, item)) || battle.containsReceivedItem(player, item);
	}
}
