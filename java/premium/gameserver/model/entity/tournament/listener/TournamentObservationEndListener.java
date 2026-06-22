package premium.gameserver.model.entity.tournament.listener;

import premium.gameserver.listener.actor.player.OnLeaveObserverModeListener;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.tournament.BattleInstance;
import premium.gameserver.model.entity.tournament.BattleObservationManager;

public class TournamentObservationEndListener implements OnLeaveObserverModeListener
{
	private final BattleInstance _battle;
	
	public TournamentObservationEndListener(BattleInstance battle)
	{
		_battle = battle;
	}
	
	@Override
	public void onLeaveObserverMode(Player player)
	{
		BattleObservationManager.onLeaveObservation(_battle, player);
	}
}
