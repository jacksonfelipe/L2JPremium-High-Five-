package premium.gameserver.listener.actor.ai;

import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.listener.AiListener;
import premium.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener
{
	public void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}
