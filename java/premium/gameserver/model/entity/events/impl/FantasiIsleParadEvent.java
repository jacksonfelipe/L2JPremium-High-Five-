package premium.gameserver.model.entity.events.impl;

import premium.commons.collections.MultiValueSet;
import premium.gameserver.model.entity.events.GlobalEvent;

public class FantasiIsleParadEvent extends GlobalEvent
{
	public FantasiIsleParadEvent(MultiValueSet<String> set)
	{
		super(set);
	}
	
	@Override
	public void reCalcNextTime(boolean isServerStarted)
	{
		clearActions();
	}
	
	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis() + 30000L;
	}
}