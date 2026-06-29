package premium.gameserver.model.base;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import premium.gameserver.Config;
import premium.gameserver.model.Creature;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

import premium.gameserver.Config;
import premium.gameserver.model.Creature;

public enum BaseStats
{
	STR
	{
		@Override
		public final int getStat(Creature actor)
		{
			return actor == null ? 1 : actor.getSTR();
		}
		
		@Override
		public final double calcBonus(Creature actor)
		{
			return actor == null ? 1. : STRbonus[actor.getSTR()];
		}
		
		@Override
		public final double calcChanceMod(Creature actor)
		{
			return Math.min(2. - Math.sqrt(calcBonus(actor)), 1.); // не более 1
		}
	},
	INT
	{
		@Override
		public final int getStat(Creature actor)
		{
			return actor == null ? 1 : actor.getINT();
		}
		
		@Override
		public final double calcBonus(Creature actor)
		{
			return actor == null ? 1. : INTbonus[actor.getINT()];
		}
	},
	DEX
	{
		@Override
		public final int getStat(Creature actor)
		{
			return actor == null ? 1 : actor.getDEX();
		}
		
		@Override
		public final double calcBonus(Creature actor)
		{
			return actor == null ? 1. : DEXbonus[actor.getDEX()];
		}
	},
	WIT
	{
		@Override
		public final int getStat(Creature actor)
		{
			return actor == null ? 1 : actor.getWIT();
		}
		
		@Override
		public final double calcBonus(Creature actor)
		{
			return actor == null ? 1. : WITbonus[actor.getWIT()];
		}
	},
	CON
	{
		@Override
		public final int getStat(Creature actor)
		{
			return actor == null ? 1 : actor.getCON();
		}
		
		@Override
		public final double calcBonus(Creature actor)
		{
			return actor == null ? 1. : CONbonus[actor.getCON()];
		}
	},
	MEN
	{
		@Override
		public final int getStat(Creature actor)
		{
			return actor == null ? 1 : actor.getMEN();
		}
		
		@Override
		public final double calcBonus(Creature actor)
		{
			return actor == null ? 1. : MENbonus[actor.getMEN()];
		}
	},
	NONE;
	
	public static final BaseStats[] VALUES = values();
	
	protected static final Logger _log = LoggerFactory.getLogger(BaseStats.class);
	
	private static final int MAX_STAT_VALUE = 100;
	
	private static final double[] STRbonus = new double[MAX_STAT_VALUE];
	private static final double[] INTbonus = new double[MAX_STAT_VALUE];
	private static final double[] DEXbonus = new double[MAX_STAT_VALUE];
	private static final double[] WITbonus = new double[MAX_STAT_VALUE];
	private static final double[] CONbonus = new double[MAX_STAT_VALUE];
	private static final double[] MENbonus = new double[MAX_STAT_VALUE];
	
	public int getStat(Creature actor)
	{
		return 1;
	}
	
	public double calcBonus(Creature actor)
	{
		return 1.;
	}
	
	public double calcChanceMod(Creature actor)
	{
		return 2. - Math.sqrt(calcBonus(actor));
	}
	
	public static final BaseStats valueOfXml(String name)
	{
		name = name.intern();
		for (BaseStats s : VALUES)
		{
			if (s.toString().equalsIgnoreCase(name))
			{
				if (s == NONE) // для упрощения
				{
					return null;
				}
				
				return s;
			}
		}
		
		throw new NoSuchElementException("Unknown name '" + name + "' for enum BaseStats");
	}
	
	static
	{
		new DocumentParser()
		{
			@Override
			public void load()
			{
				parseDatapackFile("data/attribute_bonus.xml");
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
					forEach(listNode, node ->
					{
						String nodeName = node.getNodeName();
						double[] targetArray = null;
						if (nodeName.equalsIgnoreCase("str_bonus"))
						{
							targetArray = STRbonus;
						}
						else if (nodeName.equalsIgnoreCase("int_bonus"))
						{
							targetArray = INTbonus;
						}
						else if (nodeName.equalsIgnoreCase("con_bonus"))
						{
							targetArray = CONbonus;
						}
						else if (nodeName.equalsIgnoreCase("men_bonus"))
						{
							targetArray = MENbonus;
						}
						else if (nodeName.equalsIgnoreCase("dex_bonus"))
						{
							targetArray = DEXbonus;
						}
						else if (nodeName.equalsIgnoreCase("wit_bonus"))
						{
							targetArray = WITbonus;
						}
						
						if (targetArray != null)
						{
							final double[] target = targetArray;
							forEach(node, "set", d ->
							{
								StatsSet set = parseAttributes(d);
								int i = set.getInteger("attribute");
								double val = set.getDouble("val");
								target[i] = (100 + val) / 100;
							});
						}
					});
				});
			}
		}.load();
	}
}