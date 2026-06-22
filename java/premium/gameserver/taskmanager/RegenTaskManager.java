package premium.gameserver.taskmanager;

import premium.commons.threading.RunnableImpl;
import premium.commons.threading.SteppingRunnableQueueManager;
import premium.gameserver.ThreadPoolManager;

public class RegenTaskManager extends SteppingRunnableQueueManager
{
	private static final RegenTaskManager _instance = new RegenTaskManager();
	
	public static final RegenTaskManager getInstance()
	{
		return _instance;
	}
	
	private RegenTaskManager()
	{
		super(1000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
		
		// Очистка каждые 10 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				RegenTaskManager.this.purge();
			}
			
		}, 10000L, 10000L);
	}
}