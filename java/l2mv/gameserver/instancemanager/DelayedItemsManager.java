package l2mv.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.ItemFunctions;

/**
 * Manager para processar itens atrasados da tabela items_delayed
 * @author Auto-generated
 */
public class DelayedItemsManager
{
	private static final Logger _log = LoggerFactory.getLogger(DelayedItemsManager.class);
	
	private static final int CHECK_DELAY = 60; // Verificar a cada 60 segundos
	private static final int PAYMENT_STATUS_PENDING = 0;
	private static final int PAYMENT_STATUS_DELIVERED = 1;
	
	private ScheduledFuture<?> _task;
	
	private DelayedItemsManager()
	{
		if (Config.ENABLE_DELAYED_ITEMS)
		{
			start();
		}
	}
	
	public void start()
	{
		if (_task != null)
		{
			return;
		}
		
		_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ProcessDelayedItems(), CHECK_DELAY * 1000, CHECK_DELAY * 1000);
		_log.info("DelayedItemsManager: Service started. Checking every " + CHECK_DELAY + " seconds.");
	}
	
	public void stop()
	{
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
			_log.info("DelayedItemsManager: Service stopped.");
		}
	}
	
	private class ProcessDelayedItems extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				ps = con.prepareStatement("SELECT * FROM items_delayed WHERE payment_status = ?");
				ps.setInt(1, PAYMENT_STATUS_PENDING);
				rs = ps.executeQuery();
				
				int processed = 0;
				int delivered = 0;
				
				while (rs.next())
				{
					processed++;
					int paymentId = rs.getInt("payment_id");
					int ownerId = rs.getInt("owner_id");
					int itemId = rs.getInt("item_id");
					long count = rs.getLong("count");
					int enchantLevel = rs.getInt("enchant_level");
					int attribute = rs.getInt("attribute");
					int attributeLevel = rs.getInt("attribute_level");
					int flags = rs.getInt("flags");
					String description = rs.getString("description");
					
					Player player = GameObjectsStorage.getPlayer(ownerId);
					
					if (player != null && player.isOnline())
					{
						// Jogador está online, entregar o item
						try
						{
							ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
							if (template != null)
							{
								ItemInstance item = ItemFunctions.createItem(itemId);
								item.setCount(count);
								item.setEnchantLevel(enchantLevel);
								
								if (attribute >= 0 && attributeLevel >= 0)
								{
									Element element = Element.getElementById(attribute);
									if (element != Element.NONE)
									{
										item.setAttributeElement(element, attributeLevel);
									}
								}
								
								if (flags > 0)
								{
									item.setCustomFlags(flags);
								}
								
								player.getInventory().addItem(item, "DelayedItem");
								
								if (description != null && !description.isEmpty())
								{
									player.sendMessage("Delayed Item: " + description);
								}
								
								// Marcar como entregue
								markAsDelivered(con, paymentId);
								delivered++;
								
								_log.info("DelayedItemsManager: Delivered item " + itemId + " x" + count + " to player " + player.getName() + " (ID: " + ownerId + ")");
							}
							else
							{
								_log.warn("DelayedItemsManager: Item template not found for item_id: " + itemId + " (payment_id: " + paymentId + ")");
							}
						}
						catch (Exception e)
						{
							_log.error("DelayedItemsManager: Error delivering item to player " + ownerId + " (payment_id: " + paymentId + "): " + e.getMessage(), e);
						}
					}
					// Se o jogador não estiver online, o item será entregue na próxima verificação
				}
				
				if (processed > 0)
				{
					_log.info("DelayedItemsManager: Processed " + processed + " pending items, delivered " + delivered + " items.");
				}
			}
			catch (SQLException e)
			{
				_log.error("DelayedItemsManager: Error processing delayed items: " + e.getMessage(), e);
			}
			finally
			{
				DbUtils.closeQuietly(con, ps, rs);
			}
		}
	}
	
	private void markAsDelivered(Connection con, int paymentId)
	{
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement("UPDATE items_delayed SET payment_status = ? WHERE payment_id = ?");
			ps.setInt(1, PAYMENT_STATUS_DELIVERED);
			ps.setInt(2, paymentId);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("DelayedItemsManager: Error marking payment as delivered (payment_id: " + paymentId + "): " + e.getMessage(), e);
		}
		finally
		{
			DbUtils.closeQuietly(ps);
		}
	}
	
	public static DelayedItemsManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DelayedItemsManager _instance = new DelayedItemsManager();
	}
}
