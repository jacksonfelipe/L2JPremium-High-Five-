package premium.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import java.util.HashMap;
import premium.commons.collections.MultiValueSet;
import premium.commons.data.xml.AbstractDirParser;
import premium.gameserver.Config;
import premium.gameserver.data.xml.holder.FightClubMapHolder;
import premium.gameserver.data.xml.holder.ZoneHolder;
import premium.gameserver.model.entity.events.fightclubmanager.FightClubMap;
import premium.gameserver.model.entity.events.fightclubmanager.FightClubTeamType;
import premium.gameserver.templates.ZoneTemplate;
import premium.gameserver.utils.Location;
import premium.gameserver.utils.NpcGroupLocation;

/**
 * @author VISTALL
 * @date 12:56/10.12.2010
 */
public final class FightClubMapParser extends AbstractDirParser<FightClubMapHolder>
{
	private static final FightClubMapParser _instance = new FightClubMapParser();
	
	public static FightClubMapParser getInstance()
	{
		return _instance;
	}
	
	protected FightClubMapParser()
	{
		super(FightClubMapHolder.getInstance());
	}
	
	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/fight_club_maps/");
	}
	
	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}
	
	@Override
	public String getDTDFileName()
	{
		return "maps.dtd";
	}
	
	@Override
	protected void readData(Element rootElement)
	{
		for (Iterator<Element> iterator = rootElement.elementIterator("map"); iterator.hasNext();)
		{
			Element eventElement = iterator.next();
			String name = eventElement.attributeValue("name");
			
			MultiValueSet<String> set = new MultiValueSet<>();
			set.set("name", name);
			
			for (Iterator<Element> parameterIterator = eventElement.elementIterator("parameter"); parameterIterator.hasNext();)
			{
				Element parameterElement = parameterIterator.next();
				set.set(parameterElement.attributeValue("name"), parameterElement.attributeValue("value"));
			}
			
			Map<FightClubTeamType, LinkedList<Location[]>> teamSpawns = null;
			Map<String, ZoneTemplate> territories = null;
			Location[] keyLocations = null;
			NpcGroupLocation[] npcLocations = null;
			
			for (Iterator<Element> objectIterator = eventElement.elementIterator("objects"); objectIterator.hasNext();)
			{
				Element objectElement = objectIterator.next();
				String objectsName = objectElement.attributeValue("name");
				
				FightClubTeamType teamType = FightClubTeamType.valueOf(objectElement.attributeValue("teamType", FightClubTeamType.EVERYBODY.toString()));
				if (objectsName.equals("teamSpawns"))
				{
					if (teamSpawns == null)
					{
						teamSpawns = new EnumMap<>(FightClubTeamType.class);
						for (FightClubTeamType type : FightClubTeamType.values())
						{
							teamSpawns.put(type, new LinkedList<>());
						}
					}
					teamSpawns.get(teamType).addLast(parseLocations(objectElement));
				}
				else if (objectsName.equals("territory"))
				{
					if (territories == null)
					{
						territories = new HashMap<>();
					}
					territories.putAll(parseTerritory(objectElement));
				}
				else if (objectsName.equals("keyLocations"))
				{
					keyLocations = parseLocations(objectElement);
				}
				else if (objectsName.equals("npcLocations"))
				{
					npcLocations = parseNpcLoc(objectElement);
				}
			}
			
			getHolder().addMap(new FightClubMap(set, teamSpawns, territories, keyLocations, npcLocations));
		}
	}
	
	private static Location[] parseLocations(Element element)
	{
		List<Location> locs = new ArrayList<>();
		for (Iterator<Element> objectsIterator = element.elementIterator(); objectsIterator.hasNext();)
		{
			Element objectsElement = objectsIterator.next();
			final String nodeName = objectsElement.getName();
			
			if (nodeName.equalsIgnoreCase("point"))
			{
				locs.add(Location.parse(objectsElement));
			}
		}
		
		Location[] locArray = new Location[locs.size()];
		
		for (int i = 0; i < locs.size(); i++)
		{
			locArray[i] = locs.get(i);
		}
		
		return locArray;
	}
	
	private static Map<String, ZoneTemplate> parseTerritory(Element element)
	{
		Map<String, ZoneTemplate> territories = new HashMap<>();
		for (Iterator<Element> objectsIterator = element.elementIterator(); objectsIterator.hasNext();)
		{
			Element objectsElement = objectsIterator.next();
			final String nodeName = objectsElement.getName();
			
			if (nodeName.equalsIgnoreCase("zone"))
			{
				ZoneTemplate template = ZoneHolder.getInstance().getTemplate(objectsElement.attributeValue("name"));
				territories.put(template.getName(), template);
			}
		}
		
		return territories;
	}
	
	private static NpcGroupLocation[] parseNpcLoc(Element element)
	{
		List<NpcGroupLocation> locs = new ArrayList<>();
		for (Iterator<Element> objectsIterator = element.elementIterator(); objectsIterator.hasNext();)
		{
			Element objectsElement = objectsIterator.next();
			String nodeName = objectsElement.getName();
			if (nodeName.equalsIgnoreCase("npcLoc"))
			{
				String groupName = objectsElement.attributeValue("name");
				int npcId = Integer.parseInt(objectsElement.attributeValue("npc_id"));
				locs.add(new NpcGroupLocation(groupName, npcId, Location.parse(objectsElement)));
			}
		}
		NpcGroupLocation[] locArray = new NpcGroupLocation[locs.size()];
		for (int i = 0; i < locs.size(); i++)
		{
			locArray[i] = locs.get(i);
		}
		return locArray;
	}
}
