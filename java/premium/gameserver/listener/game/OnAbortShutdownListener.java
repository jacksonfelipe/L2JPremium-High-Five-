package premium.gameserver.listener.game;

import premium.gameserver.Shutdown;
import premium.gameserver.listener.GameListener;

public interface OnAbortShutdownListener extends GameListener
{
	void onAbortShutdown(Shutdown.ShutdownMode p0, int p1);
}
