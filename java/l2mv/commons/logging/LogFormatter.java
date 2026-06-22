package l2mv.commons.logging;

import java.util.Calendar;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter
{
	String newline = System.getProperty("line.separator");
	
	@Override
	public String format(LogRecord record)
	{
		
		String text = "";
		text += " ";
		text += " " + record.getMessage();
		text += newline;
		return text;
	}
	
	public String getDate(Calendar c, int i)
	{
		int intResult = c.get(i);
		if (i == Calendar.MONTH)
		{
			intResult++;
		}
		String result = String.valueOf(intResult);
		if (result.length() == 4)
		{
			result = result.substring(2);
		}
		if (result.length() == 1)
		{
			result = "0" + result;
		}
		return result;
	}
}
