package premium.gameserver.listener.game;

import premium.gameserver.listener.GameListener;

public interface OnDayNightChangeListener extends GameListener
{
	public void onDay();
	
	public void onNight();
}
