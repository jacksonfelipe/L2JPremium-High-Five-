package premium.gameserver.model.entity.tournament.listener;

import premium.gameserver.listener.actor.player.OnPlayerSummonPetListener;
import premium.gameserver.model.Player;
import premium.gameserver.model.Summon;
import premium.gameserver.model.entity.tournament.ActiveBattleManager;
import premium.gameserver.model.entity.tournament.BattleInstance;

public class TournamentSpawnSummonListener implements OnPlayerSummonPetListener
{
	private final BattleInstance _battleInstance;
	
	public TournamentSpawnSummonListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}
	
	@Override
	public void onSummonPet(Player player, Summon summon)
	{
		ActiveBattleManager.onSpawnedSummon(_battleInstance, summon, true);
	}
}
