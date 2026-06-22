package premium.gameserver.model.entity.tournament.permission;

import premium.gameserver.ConfigHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.actor.player.EnchantItemPermission;

public class TournamentEnchantItemPermission implements EnchantItemPermission
{
	@Override
	public boolean canEnchantItem(Player actor, ItemInstance item, ItemInstance scroll, ItemInstance catalyst)
	{
		return ConfigHolder.getBool("TournamentAllowEnchanting");
	}
	
	@Override
	public void sendPermissionDeniedError(Player actor, ItemInstance item, ItemInstance scroll, ItemInstance catalyst)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.AttributeItem", new Object[0]);
	}
}
