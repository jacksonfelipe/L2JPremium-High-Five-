package premium.gameserver.listener.event;

import premium.gameserver.listener.EventListener;
import premium.gameserver.model.entity.events.GlobalEvent;

public interface OnStartStopListener extends EventListener
{
	void onStart(GlobalEvent event);
	
	void onStop(GlobalEvent event);
}
