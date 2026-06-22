package premium.gameserver.model.entity.tournament.listener;

import premium.gameserver.listener.actor.OnCurrentHpDamageListener;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.model.entity.tournament.ActiveBattleManager;
import premium.gameserver.model.entity.tournament.BattleInstance;

public class TournamentReceiveDamageListener implements OnCurrentHpDamageListener
{
	private final BattleInstance _battleInstance;
	
	public TournamentReceiveDamageListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}
	
	@Override
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
	{
		ActiveBattleManager.onReceivedDamage(_battleInstance, attacker, actor, damage);
	}
}
