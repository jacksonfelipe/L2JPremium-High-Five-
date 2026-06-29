package premium.gameserver.data.xml.holder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import premium.gameserver.Config;
import premium.gameserver.model.items.TradeItem;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.templates.item.ItemTemplate;
import premium.gameserver.utils.DocumentParser;

//import premium.commons.crypt.CryptUtil;
import premium.gameserver.Config;
import premium.gameserver.model.items.TradeItem;
import premium.gameserver.templates.item.ItemTemplate;

public class BuyListHolder extends DocumentParser
{
	private static final Logger _log = LoggerFactory.getLogger(BuyListHolder.class);
	private static BuyListHolder _instance;
	
	private Map<Integer, NpcTradeList> _lists;
	
	public static BuyListHolder getInstance()
	{
		if (_instance == null)
		{
			_instance = new BuyListHolder();
		}
		return _instance;
	}
	
	public static void reload()
	{
		_instance = new BuyListHolder();
	}
	
	private BuyListHolder()
	{
		_lists = new HashMap<>();
		load();
	}
	
	@Override
	public void load()
	{
		_lists.clear();
		parseDatapackFile("data/merchant_filelists.xml");
		_log.info("TradeController: Loaded " + _lists.size() + " Buylists.");
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
		
	@Override
	protected void parseDocument(Document doc)
	{
		if (getCurrentFile().getName().equals("merchant_filelists.xml"))
		{
			forEach(doc, "list", listNode ->
			{
				forEach(listNode, "file", fileNode ->
				{
					StatsSet set = parseAttributes(fileNode);
					parseDatapackFile("data/" + set.getString("name"));
				});
			});
			return;
		}
		
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "tradelist", d2 ->
			{
				StatsSet tlSet = parseAttributes(d2);
				String[] npcs = tlSet.getString("npc").split(";");
				String[] shopIds = tlSet.getString("shop").split(";");
				
				String[] markups = new String[0];
				boolean haveMarkups = false;
				if (tlSet.isSet("markup"))
				{
					markups = tlSet.getString("markup").split(";");
					haveMarkups = true;
				}
				
				int size = npcs.length;
				if (!haveMarkups)
				{
					markups = new String[size];
					for (int i = 0; i < size; i++)
					{
						markups[i] = "0";
					}
				}
				
				if (shopIds.length != size || markups.length != size)
				{
					_log.warn("Do not correspond to the size of arrays in " + getCurrentFile().getName());
					return;
				}
				
				for (int n = 0; n < size; n++)
				{
					final int npc_id = Integer.parseInt(npcs[n]);
					final int shop_id = Integer.parseInt(shopIds[n]);
					final double markup = npc_id > 0 ? 1. + Double.parseDouble(markups[n]) / 100. : 0.;
					NpcTradeList tl = new NpcTradeList(shop_id);
					tl.setNpcId(npc_id);
					
					forEach(d2, "item", i ->
					{
						StatsSet itemSet = parseAttributes(i);
						final int itemId = itemSet.getInteger("id");
						final ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
						if (template == null)
						{
							_log.warn("Template not found for itemId: " + itemId + " for shop " + shop_id);
							return;
						}
						if (!checkItem(template))
						{
							return;
						}
						
						long price = itemSet.isSet("price") ? itemSet.getLong("price") : Math.round(template.getReferencePrice() * markup);
						TradeItem item = new TradeItem();
						item.setItemId(itemId);
						
						final int itemCount = itemSet.getInteger("count", 0);
						// Время респауна задается минутах
						final int itemRechargeTime = itemSet.getInteger("time", 0);
						
						item.setOwnersPrice(price);
						item.setCount(itemCount);
						item.setCurrentValue(itemCount);
						item.setLastRechargeTime((int) (System.currentTimeMillis() / 60000));
						item.setRechargeTime(itemRechargeTime);
						tl.addItem(item);
					});
					_lists.put(shop_id, tl);
				}
			});
		});
	}
	
	public boolean checkItem(ItemTemplate template)
	{
		if (template.isCommonItem() && !Config.ALT_ALLOW_SELL_COMMON)
		{
			return false;
		}
		if (template.isEquipment() && !template.isForPet() && Config.ALT_SHOP_PRICE_LIMITS.length > 0)
		{
			for (int i = 0; i < Config.ALT_SHOP_PRICE_LIMITS.length; i += 2)
			{
				if (template.getBodyPart() == Config.ALT_SHOP_PRICE_LIMITS[i])
				{
					if (template.getReferencePrice() > Config.ALT_SHOP_PRICE_LIMITS[i + 1])
					{
						return false;
					}
					break;
				}
			}
		}
		if (Config.ALT_SHOP_UNALLOWED_ITEMS.length > 0)
		{
			for (int i : Config.ALT_SHOP_UNALLOWED_ITEMS)
			{
				if (template.getItemId() == i)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public NpcTradeList getBuyList(int listId)
	{
		return _lists.get(listId);
	}
	
	public void addToBuyList(int listId, NpcTradeList list)
	{
		_lists.put(listId, list);
	}
	
	public static class NpcTradeList
	{
		private List<TradeItem> tradeList = new ArrayList<>();
		private int _id;
		private int _npcId;
		
		public NpcTradeList(int id)
		{
			_id = id;
		}
		
		public int getListId()
		{
			return _id;
		}
		
		public void setNpcId(int id)
		{
			_npcId = id;
		}
		
		public int getNpcId()
		{
			return _npcId;
		}
		
		public void addItem(TradeItem ti)
		{
			tradeList.add(ti);
		}
		
		public synchronized List<TradeItem> getItems()
		{
			List<TradeItem> result = new ArrayList<>();
			long currentTime = System.currentTimeMillis() / 60000L;
			for (TradeItem ti : tradeList)
			{
				// А не пора ли обновить количество лимитированных предметов в трейд листе?
				if (ti.isCountLimited())
				{
					if (ti.getCurrentValue() < ti.getCount() && ti.getLastRechargeTime() + ti.getRechargeTime() <= currentTime)
					{
						ti.setLastRechargeTime(ti.getLastRechargeTime() + ti.getRechargeTime());
						ti.setCurrentValue(ti.getCount());
					}
					
					if (ti.getCurrentValue() == 0)
					{
						continue;
					}
				}
				
				result.add(ti);
			}
			
			return result;
		}
		
		public TradeItem getItemByItemId(int itemId)
		{
			for (TradeItem ti : tradeList)
			{
				if (ti.getItemId() == itemId)
				{
					return ti;
				}
			}
			return null;
		}
		
		public synchronized void updateItems(List<TradeItem> buyList)
		{
			for (TradeItem ti : buyList)
			{
				TradeItem ic = getItemByItemId(ti.getItemId());
				
				if (ic.isCountLimited())
				{
					ic.setCurrentValue(Math.max(ic.getCurrentValue() - ti.getCount(), 0));
				}
			}
		}
	}
}