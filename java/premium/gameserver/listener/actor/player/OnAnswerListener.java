package premium.gameserver.listener.actor.player;

import premium.gameserver.listener.PlayerListener;

public interface OnAnswerListener extends PlayerListener
{
	void sayYes();
	
	void sayNo();
}
