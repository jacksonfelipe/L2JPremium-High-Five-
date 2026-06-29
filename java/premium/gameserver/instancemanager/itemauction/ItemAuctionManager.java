package premium.gameserver.instancemanager.itemauction;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import gnu.trove.map.hash.TIntObjectHashMap;
import premium.commons.dbutils.DbUtils;
import premium.commons.time.cron.SchedulingPattern;
import premium.gameserver.Config;
import premium.gameserver.database.DatabaseFactory;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

/**
 * @author n0nam3
 */
public class ItemAuctionManager extends DocumentParser
{
	private static Logger LOG = LoggerFactory.getLogger(ItemAuctionManager.class);
	
	private static ItemAuctionManager _instance;
	
	public static final ItemAuctionManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new ItemAuctionManager();
			if (Config.ALT_ITEM_AUCTION_ENABLED)
			{
				_instance.load();
			}
		}
		return _instance;
	}
	
	private final TIntObjectHashMap<ItemAuctionInstance> _managerInstances = new TIntObjectHashMap<>();
	private final AtomicInteger _nextId = new AtomicInteger();
	
	private ItemAuctionManager()
	{
		LOG.info("Initializing ItemAuctionManager");
	}
	
	@Override
	public void load()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1");
			rset = statement.executeQuery();
			if (rset.next())
			{
				_nextId.set(rset.getInt(1));
			}
		}
		catch (SQLException e)
		{
			LOG.error("ItemAuctionManager: Failed loading auctions.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		
		File file = new File(Config.DATAPACK_ROOT, "data/item_auctions.xml");
		if (!file.exists())
		{
			LOG.warn("ItemAuctionManager: Missing item_auctions.xml!");
			return;
		}
		
		parseDatapackFile("data/item_auctions.xml");
		LOG.info("ItemAuctionManager: Loaded " + _managerInstances.size() + " instance(s).");
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
			forEach(listNode, "instance", nb ->
			{
				StatsSet instSet = parseAttributes(nb);
				int instanceId = instSet.getInteger("id");
				
				if (_managerInstances.containsKey(instanceId))
				{
					throw new IllegalArgumentException("Duplicate instanceId " + instanceId);
				}
				
				SchedulingPattern dateTime;
				try
				{
					dateTime = new SchedulingPattern(instSet.getString("schedule"));
				}
				catch (SchedulingPattern.InvalidPatternException e)
				{
					throw new IllegalArgumentException("Invalid schedule pattern for instanceId " + instanceId, e);
				}
				
				List<AuctionItem> items = new ArrayList<>();
				
				forEach(nb, "item", nc ->
				{
					StatsSet itemSet = parseAttributes(nc);
					int auctionItemId = itemSet.getInteger("auctionItemId");
					int auctionLenght = itemSet.getInteger("auctionLenght");
					long auctionInitBid = itemSet.getLong("auctionInitBid");
					
					int itemId = itemSet.getInteger("itemId");
					int itemCount = itemSet.getInteger("itemCount");
					boolean altByItem = itemSet.getBool("altByItem", false);
					
					if (auctionLenght < 1)
					{
						throw new IllegalArgumentException("auctionLenght < 1 for instanceId: " + instanceId + ", itemId " + itemId);
					}
					
					for (AuctionItem tmp : items)
					{
						if (tmp.getAuctionItemId() == auctionItemId)
						{
							throw new IllegalArgumentException("Dublicated auction item id " + auctionItemId + "for instanceId: " + instanceId);
						}
					}
					
					StatsSet itemExtra = new StatsSet();
					forEach(nc, "extra", nd ->
					{
						NamedNodeMap nad = nd.getAttributes();
						for (int i = nad.getLength(); i-- > 0;)
						{
							Node n = nad.item(i);
							if (n != null)
							{
								itemExtra.set(n.getNodeName(), n.getNodeValue());
							}
						}
					});
					
					AuctionItem item = new AuctionItem(auctionItemId, auctionLenght, auctionInitBid, itemId, itemCount, altByItem, itemExtra);
					items.add(item);
				});
				
				if (items.isEmpty())
				{
					throw new IllegalArgumentException("No items defined for instanceId: " + instanceId);
				}
				
				ItemAuctionInstance instance = new ItemAuctionInstance(instanceId, dateTime, items);
				_managerInstances.put(instanceId, instance);
			});
		});
	}
	
	public void shutdown()
	{
		ItemAuctionInstance[] instances = _managerInstances.values(new ItemAuctionInstance[_managerInstances.size()]);
		for (ItemAuctionInstance instance : instances)
		{
			instance.shutdown();
		}
	}
	
	public ItemAuctionInstance getManagerInstance(int instanceId)
	{
		return _managerInstances.get(instanceId);
	}
	
	public int getNextId()
	{
		return _nextId.incrementAndGet();
	}
	
	public void deleteAuction(int auctionId)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM item_auction WHERE auctionId=?");
			statement.setInt(1, auctionId);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=?");
			statement.setInt(1, auctionId);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			LOG.error("ItemAuctionManager: Failed deleting auction: " + auctionId, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}