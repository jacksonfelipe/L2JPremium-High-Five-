package premium.gameserver.listener.actor.ai;

import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.listener.AiListener;
import premium.gameserver.model.Creature;

public interface OnAiEventListener extends AiListener
{
	public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args);
}
