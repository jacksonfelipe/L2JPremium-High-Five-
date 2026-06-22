package ai.freya;

import bosses.ValakasManager;
import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.Mystic;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;

public class ValakasMinion extends Mystic
{
	public ValakasMinion(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}
	
	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		for (Player p : ValakasManager.getZone().getInsidePlayers())
		{
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
		}
	}
}