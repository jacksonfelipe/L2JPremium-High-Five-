package premium.gameserver.dao;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import java.util.concurrent.ConcurrentHashMap;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

public class EmotionsTable extends DocumentParser
{
	private static final Logger _log = Logger.getLogger(EmotionsTable.class.getName());
	private static Map<String, Integer> _emotions = new ConcurrentHashMap<>();
	
	private static EmotionsTable _instance;
	
	public static EmotionsTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new EmotionsTable();
		}
		return _instance;
	}
	
	public static void init()
	{
		getInstance().load();
	}
	
	@Override
	public void load()
	{
		_emotions.clear();
		parseDatapackFile("data/emotions.xml");
	}
	
	@Override
	protected void parseDocument()
	{
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "emo", d1 ->
			{
				StatsSet set = parseAttributes(d1);
				_emotions.put(set.getString("text"), set.getInteger("emotionId"));
			});
		});
	}
	
	public static Map<String, Integer> getEmoticons()
	{
		return _emotions;
	}
	
	public static int containsEmotion(String text)
	{
		for (Entry<String, Integer> emot : _emotions.entrySet())
		{
			if (text.contains(emot.getKey()))
			{
				return emot.getValue();
			}
		}
		return -1;
	}
}
