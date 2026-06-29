package premium.gameserver.inertiax.model.tables;

import java.io.File;
import java.util.HashMap;

import org.w3c.dom.Document;

import premium.gameserver.inertiax.model.templates.InertiaConfiguration;

import premium.gameserver.utils.DocumentParser;

public class InertiaConfigurationTable extends DocumentParser
{
	private final HashMap<String, InertiaConfiguration> inertiaTemplates = new HashMap<>();

	private InertiaConfigurationTable()
	{
		load();
	}

	@Override
	public void load()
	{
		inertiaTemplates.clear();
		parseDirectory(new File("./data/xml/inertia/configurations/"));
		System.out.println("InertiaConfigurationTable Loaded " + inertiaTemplates.size() + " templates.");
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		forEach(doc, "configurations", configurationsNode ->
		{
			forEach(configurationsNode, "configuration", n1 ->
			{
				final InertiaConfiguration inertiaExtTemplate = new InertiaConfiguration(n1);
				final String templateId = inertiaExtTemplate.getTemplateId();
				inertiaTemplates.put(templateId, inertiaExtTemplate);
			});
		});
	}
	
	public void reload()
	{
		load();
	}
	
	public InertiaConfiguration getById(final String templateId)
	{
		return inertiaTemplates.get(templateId);
	}

	public static class InstanceHolder
	{
		private static final InertiaConfigurationTable _instance = new InertiaConfigurationTable();
	}

	public static InertiaConfigurationTable getInstance()
	{
		return InstanceHolder._instance;
	}
}
