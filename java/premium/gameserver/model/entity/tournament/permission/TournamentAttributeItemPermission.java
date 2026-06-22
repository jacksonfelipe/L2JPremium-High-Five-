package premium.gameserver.model.entity.tournament.permission;

import premium.gameserver.ConfigHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.actor.player.AttributeItemPermission;

public class TournamentAttributeItemPermission implements AttributeItemPermission
{
	@Override
	public boolean canAttributeItem(Player actor, ItemInstance item, ItemInstance stone)
	{
		return ConfigHolder.getBool("TournamentAllowMakingAttribute");
	}
	
	@Override
	public void sendPermissionDeniedError(Player actor, ItemInstance item, ItemInstance stone)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.AttributeItem", new Object[0]);
	}
}
