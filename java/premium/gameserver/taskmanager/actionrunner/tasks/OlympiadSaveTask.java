package premium.gameserver.taskmanager.actionrunner.tasks;

import premium.gameserver.model.entity.olympiad.OlympiadDatabase;

public class OlympiadSaveTask extends AutomaticTask
{
	public OlympiadSaveTask()
	{
		super();
	}
	
	@Override
	public void doTask()  
	{
		OlympiadDatabase.save();
	}
	
	@Override
	public long reCalcTime(boolean start)
	{
		return System.currentTimeMillis() + 600000L;
	}
}
