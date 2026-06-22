package premium.gameserver.model.entity.tournament.permission;

import premium.gameserver.data.StringHolder;
import premium.gameserver.model.Playable;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.permission.actor.player.LoseItemPermission;

public class TournamentLoseItemPermission implements LoseItemPermission
{
	@Override
	public boolean canLoseItem(Playable actor, ItemInstance item)
	{
		return false;
	}
	
	@Override
	public String getPermissionDeniedError(Playable actor, ItemInstance item)
	{
		return StringHolder.getNotNull(actor.getPlayer(), "Tournament.NotAllowed.LoseItem", new Object[0]);
	}
}
