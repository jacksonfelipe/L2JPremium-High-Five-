package premium.gameserver.tables;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import gnu.trove.map.hash.TIntObjectHashMap;
import premium.gameserver.Config;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.templates.item.ItemTemplate;
import premium.gameserver.utils.DocumentParser;

import gnu.trove.map.hash.TIntObjectHashMap;
import premium.gameserver.Config;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.templates.item.ItemTemplate;

public class EnchantHPBonusTable extends DocumentParser
{
	private static Logger _log = LoggerFactory.getLogger(EnchantHPBonusTable.class);
	
	private final TIntObjectHashMap<Integer[]> _armorHPBonus = new TIntObjectHashMap<>();
	
	private int _onepieceFactor = 100;
	
	private static EnchantHPBonusTable _instance = new EnchantHPBonusTable();
	
	public static EnchantHPBonusTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new EnchantHPBonusTable();
		}
		return _instance;
	}
	
	public void reload()
	{
		_instance = new EnchantHPBonusTable();
	}
	
	private EnchantHPBonusTable()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_armorHPBonus.clear();
		parseDatapackFile("data/enchant_bonus.xml");
		_log.info("EnchantHPBonusTable: Loaded bonuses for " + _armorHPBonus.size() + " grades.");
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, d ->
			{
				StatsSet set = parseAttributes(d);
				if ("options".equalsIgnoreCase(d.getNodeName()))
				{
					_onepieceFactor = set.getInteger("onepiece_factor", _onepieceFactor);
				}
				else if ("enchant_bonus".equalsIgnoreCase(d.getNodeName()))
				{
					if (!set.isSet("grade") || !set.isSet("values"))
					{
						_log.info("EnchantHPBonusTable: Missing grade or values, skipping");
						return;
					}
					
					int grade = set.getInteger("grade");
					StringTokenizer st = new StringTokenizer(set.getString("values"), ",");
					int tokenCount = st.countTokens();
					Integer[] bonus = new Integer[tokenCount];
					for (int i = 0; i < tokenCount; i++)
					{
						try
						{
							bonus[i] = Integer.decode(st.nextToken().trim());
						}
						catch (NumberFormatException e)
						{
							_log.info("EnchantHPBonusTable: Bad Hp value!! grade: " + grade + " token: " + i);
							bonus[i] = 0;
						}
					}
					_armorHPBonus.put(grade, bonus);
				}
			});
		});
	}
	
	public final int getHPBonus(ItemInstance item)
	{
		final Integer[] values;
		
		if (item.getEnchantLevel() == 0)
		{
			return 0;
		}
		
		values = _armorHPBonus.get(item.getTemplate().getCrystalType().externalOrdinal);
		
		if (values == null || values.length == 0)
		{
			return 0;
		}
		
		int bonus = values[Math.min(item.getEnchantLevel(), values.length) - 1];
		if (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
		{
			bonus = (int) (bonus * _onepieceFactor / 100.0D);
		}
		
		return bonus;
	}
}
