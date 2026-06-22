package premium.gameserver.model.entity.events.actions;

import premium.gameserver.model.entity.events.EventAction;
import premium.gameserver.model.entity.events.GlobalEvent;

public class RefreshAction implements EventAction
{
	private final String _name;
	
	public RefreshAction(String name)
	{
		_name = name;
	}
	
	@Override
	public void call(GlobalEvent event)
	{
		event.refreshAction(_name);
	}
}
