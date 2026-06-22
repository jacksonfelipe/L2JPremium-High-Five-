package premium.gameserver.listener.game;

import premium.gameserver.Shutdown;
import premium.gameserver.listener.GameListener;

public interface OnShutdownListener extends GameListener
{
	void onShutdown(Shutdown.ShutdownMode p0);
}
