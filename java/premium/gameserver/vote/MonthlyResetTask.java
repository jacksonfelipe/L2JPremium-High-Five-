package premium.gameserver.vote;

import java.util.Calendar;

public class MonthlyResetTask
{
	 
	public static void getInstance()
	{
 
	}
	
	public static long getValidationTime()
	{
		Calendar cld = Calendar.getInstance();
		cld.set(5, 1);
		long time = cld.getTimeInMillis();
		if (System.currentTimeMillis() - time <= 0L)
		{
			return cld.getTimeInMillis() - System.currentTimeMillis();
		}
		return 0L;
	}
}