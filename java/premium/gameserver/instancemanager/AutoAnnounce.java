package premium.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import premium.gameserver.Announcements;
import premium.gameserver.Config;
import premium.gameserver.model.AutoAnnounces;

import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

public class AutoAnnounce extends DocumentParser implements Runnable
{
	private static final Logger LOG = LoggerFactory.getLogger(AutoAnnounce.class);
	private static AutoAnnounce _instance;
	
	static HashMap<Integer, AutoAnnounces> _lists;
	
	public static AutoAnnounce getInstance()
	{
		if (_instance == null)
		{
			_instance = new AutoAnnounce();
		}
		return _instance;
	}
	
	public static void reload()
	{
		_instance = new AutoAnnounce();
	}
	
	public AutoAnnounce()
	{
		_lists = new HashMap<>();
		LOG.info("AutoAnnounce: Initializing");
		load();
		LOG.info("AutoAnnounce: Loaded " + _lists.size() + " announce.");
	}
	
	@Override
	public void load()
	{
		_lists.clear();
		File file = new File(Config.DATAPACK_ROOT, "/config/autoannounce.xml");
		if (!file.exists())
		{
			LOG.warn("AutoAnnounce: NO FILE (./config/autoannounce.xml)");
			return;
		}
		
		parseFile(file);
		LOG.info("AutoAnnounce: Load OK");
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		int[] counterAnnounce = { 0 };
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "announce", d ->
			{
				StatsSet attrs = parseAttributes(d);
				int delay = attrs.getInteger("delay");
				int repeat = attrs.getInteger("repeat");
				AutoAnnounces aa = new AutoAnnounces(counterAnnounce[0]);
				
				ArrayList<String> msg = new ArrayList<>();
				forEach(d, "message", cd ->
				{
					msg.add(parseAttributes(cd).getString("text"));
				});
				
				aa.setAnnounce(delay, repeat, msg);
				_lists.put(counterAnnounce[0], aa);
				counterAnnounce[0]++;
			});
		});
	}
	
	@Override
	public void run()
	{
		if (_lists.size() <= 0)
		{
			return;
		}
		for (int i = 0; i < _lists.size(); i++)
		{
			if (_lists.get(i).canAnnounce())
			{
				ArrayList<String> msg = _lists.get(i).getMessage();
				for (int c = 0; c < msg.size(); c++)
				{
					Announcements.getInstance().announceToAll(msg.get(c));
				}
				_lists.get(i).updateRepeat();
			}
		}
	}
}