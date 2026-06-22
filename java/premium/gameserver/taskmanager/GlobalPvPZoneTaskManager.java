package premium.gameserver.taskmanager;

import java.util.concurrent.ScheduledFuture;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.Config;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.cache.ImagesCache;
import premium.gameserver.data.htm.HtmCache;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.TutorialShowHtml;

public class GlobalPvPZoneTaskManager extends RunnableImpl
{
	private static final long DELAY = 1000L;
	
	private ScheduledFuture<?> _feature;
	private boolean _globalPvPOn = false;
	
	public void startThread()
	{
		if (_feature == null || _feature.isCancelled())
		{
			_feature = ThreadPoolManager.getInstance().scheduleAtFixedDelay(this, DELAY, DELAY);
		}
	}
	
	public void stopThread()
	{
		if (_feature != null && !_feature.isCancelled())
		{
			_feature.cancel(true);
		}
	}
	
	@Override
	public void runImpl()
	{
		if (isGlobalPvpOn())
		{
			givePvPFlags();
		}
	}
	
	private static void givePvPFlags()
	{
		for (Player player : GameObjectsStorage.getAllPlayersCopy())
		{
			if (player.isInActiveGlobalPvPZone())
			{
				player.startPvPFlag(null);
			}
		}
	}
	
	public void setGlobalPvpOn(boolean globalPvPOn)
	{
		_globalPvPOn = globalPvPOn;
	}
	
	public boolean isGlobalPvpOn()
	{
		return _globalPvPOn;
	}
	
	public void sendMainHtmlToPlayer(Player player)
	{
		String content = HtmCache.getInstance().getNotNull("events/globalPvpEvent/main.htm", player);
		content = content.replace("%serverName%", Config.SERVER_NAME);
		content = ImagesCache.getInstance().sendUsedImages(content, player);
		player.sendPacket(new TutorialShowHtml(content));
	}
	
	public static GlobalPvPZoneTaskManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		private static final GlobalPvPZoneTaskManager instance = new GlobalPvPZoneTaskManager();
	}
}
