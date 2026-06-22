package premium.gameserver.model.entity.tournament.listener;

import premium.gameserver.listener.actor.OnStatusUpdateBroadcastListener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Playable;
import premium.gameserver.model.entity.tournament.BattleInstance;
import premium.gameserver.model.entity.tournament.BattleObservationManager;

public class TournamentBroadcastStatusListener implements OnStatusUpdateBroadcastListener
{
	private final BattleInstance battleInstance;
	
	public TournamentBroadcastStatusListener(BattleInstance battleInstance)
	{
		super();
		this.battleInstance = battleInstance;
	}
	
	@Override
	public void onStatusUpdate(Creature creature)
	{
		if (creature.isPlayable())
		{
			BattleObservationManager.broadcastFighterStatusUpdate(battleInstance, (Playable) creature);
			return;
		}
		throw new AssertionError(creature + " present in " + this.getClass().getName());
	}
}
