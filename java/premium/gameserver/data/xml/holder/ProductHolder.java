package premium.gameserver.data.xml.holder;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import premium.gameserver.Config;
import premium.gameserver.model.ProductItem;
import premium.gameserver.model.ProductItemComponent;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

import premium.gameserver.Config;
import premium.gameserver.model.ProductItem;
import premium.gameserver.model.ProductItemComponent;

public class ProductHolder extends DocumentParser
{
	private static Logger _log = LoggerFactory.getLogger(ProductHolder.class.getName());
	TreeMap<Integer, ProductItem> _itemsList;
	
	private static ProductHolder _instance = new ProductHolder();
	
	public static ProductHolder getInstance()
	{
		if (_instance == null)
		{
			_instance = new ProductHolder();
		}
		return _instance;
	}
	
	public void reload()
	{
		_instance = new ProductHolder();
	}
	
	private ProductHolder()
	{
		_itemsList = new TreeMap<>();
		load();
	}
	
	@Override
	public void load()
	{
		_itemsList.clear();
		parseDatapackFile("data/item-mall.xml");
		_log.info(String.format("ProductItemTable: Loaded %d product item on sale.", _itemsList.size()));
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
			forEach(listNode, "product", d1 ->
			{
				StatsSet set = parseAttributes(d1);
				if (!set.getBool("on_sale", false))
				{
					return;
				}
				
				int productId = set.getInteger("id");
				int category = set.getInteger("category", 5);
				int price = set.getInteger("price", 0);
				
				boolean isEvent = set.getBool("is_event", false);
				boolean isBest = set.getBool("is_best", false);
				boolean isNew = set.getBool("is_new", false);
				
				int tabId = getProductTabId(isEvent, isBest, isNew);
				
				long startTimeSale = set.isSet("sale_start_date") ? getMillisecondsFromString(set.getString("sale_start_date")) : 0;
				long endTimeSale = set.isSet("sale_end_date") ? getMillisecondsFromString(set.getString("sale_end_date")) : 0;
				
				ArrayList<ProductItemComponent> components = new ArrayList<>();
				ProductItem pr = new ProductItem(productId, category, price, tabId, startTimeSale, endTimeSale);
				
				forEach(d1, "component", t1 ->
				{
					StatsSet compSet = parseAttributes(t1);
					int item_id = compSet.getInteger("item_id");
					int count = compSet.getInteger("count");
					components.add(new ProductItemComponent(item_id, count));
				});
				
				pr.setComponents(components);
				_itemsList.put(productId, pr);
			});
		});
	}
	
	private static int getProductTabId(boolean isEvent, boolean isBest, boolean isNew)
	{
		// TODO: Заюзать isNew
		if (isEvent && isBest)
		{
			return 3;
		}
		
		if (isEvent)
		{
			return 1;
		}
		
		if (isBest)
		{
			return 2;
		}
		
		return 4;
	}
	
	private static long getMillisecondsFromString(String datetime)
	{
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		try
		{
			Date time = df.parse(datetime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			
			return calendar.getTimeInMillis();
		}
		catch (ParseException e)
		{
			_log.error("Error while gettingMillisecondsFromString ", e);
		}
		
		return 0L;
	}
	
	public Collection<ProductItem> getAllItems()
	{
		return _itemsList.values();
	}
	
	public ProductItem getProduct(int id)
	{
		return _itemsList.get(id);
	}
}
