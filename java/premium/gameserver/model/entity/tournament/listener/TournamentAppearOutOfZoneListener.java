package premium.gameserver.model.entity.tournament.listener;

import premium.gameserver.listener.actor.player.OnTeleportedListener;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.utils.Location;

public class TournamentAppearOutOfZoneListener implements OnTeleportedListener
{
	private final Location _locationToBeTeleported;
	private final Reflection _reflection;
	
	public TournamentAppearOutOfZoneListener(Location locationToBeTeleported, Reflection reflection)
	{
		_locationToBeTeleported = locationToBeTeleported;
		_reflection = reflection;
	}
	
	@Override
	public void onTeleported(Player player)
	{
		player.removeListener(this);
		player.teleToLocation(_locationToBeTeleported, _reflection);
	}
}
