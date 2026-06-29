package premium.gameserver.model.entity.achievements;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import premium.gameserver.Config;
import premium.gameserver.data.htm.HtmCache;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.TutorialCloseHtml;
import premium.gameserver.network.serverpackets.TutorialShowHtml;

import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

/**
 * @author Nik (total rework)
 */
public class Achievements extends DocumentParser
{
	// id-max
	private final Map<Integer, Integer> _achievementMaxLevels = new ConcurrentHashMap<>();
	private final List<AchievementCategory> _achievementCategories = new LinkedList<>();
	private static Achievements _instance;
	
	private static final Logger _log = LoggerFactory.getLogger(Achievements.class);
	
	public Achievements()
	{
		load();
	}
	
	public void onBypass(Player player, String bypass, String[] cm)
	{
		if (bypass.startsWith("_bbs_achievements_cat"))
		{
			generatePage(player, Integer.parseInt(cm[1]), Integer.parseInt(cm[2]));
		}
		else if (bypass.equals("_bbs_achievements_close"))
		{
			player.sendPacket(TutorialCloseHtml.STATIC);
		}
		else if (bypass.startsWith("_bbs_achievements"))
		{
			checkAchievementRewards(player);
			generatePage(player);
		}
		else
		{
			_log.warn("Invalid achievements bypass: " + bypass);
		}
	}
	
	public void generatePage(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		String achievements = HtmCache.getInstance().getNotNull("achievements/Achievements.htm", player);
		
		String ac = "";
		for (AchievementCategory cat : _achievementCategories)
		{
			ac += cat.getHtml(player);
		}
		
		achievements = achievements.replace("%categories%", ac);
		
		// player.sendPacket(html);
		player.sendPacket(new TutorialShowHtml(achievements));
	}
	
	public void generatePage(Player player, int category, int page)
	{
		if (player == null)
		{
			return;
		}
		
		String FULL_PAGE = HtmCache.getInstance().getNotNull("achievements/inAchievements.htm", player);
		
		final int totalpages = (int) (Math.ceil(player.getAchievements(category).size() / 5.0));
		
		FULL_PAGE = FULL_PAGE.replaceAll("%back%", page == 1 ? "<button value=\"\" action=\"bypass _bbs_achievements\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">" : "<button value=\"\" action=\"bypass _bbs_achievements_cat " + category + " " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
		FULL_PAGE = FULL_PAGE.replaceAll("%more%", totalpages <= page ? "&nbsp;" : "<button value=\"\" action=\"bypass _bbs_achievements_cat " + category + " " + (page + 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");
		
		AchievementCategory cat = _achievementCategories.stream().filter(ctg -> ctg.getCategoryId() == category).findAny().orElse(null);
		if (cat == null)
		{
			_log.warn("Achievements: getCatById - cat - is null, return. for " + player.getName());
			return;
		}
		
		int all = 0;
		String achievementsHTML = "";
		Map<Integer, Integer> playerAchievements = player.getAchievements(category);
		for (Entry<Integer, Integer> entry : playerAchievements.entrySet())
		{
			all++;
			if ((all > (page * 5)) || (all <= ((page - 1) * 5)))
			{
				continue;
			}
			
			int aId = entry.getKey();
			int nextLevel = (entry.getValue() + 1) >= getMaxLevel(aId) ? getMaxLevel(aId) : (entry.getValue() + 1);
			Achievement a = getAchievement(aId, Math.max(1, nextLevel));
			
			if (a == null)
			{
				_log.warn("Achievements: GetAchievement - a - is null, return. for " + player.getName());
				return;
			}
			
			long playerPoints = player.getCounters().getPoints(a.getType());
			achievementsHTML += a.getHtml(player, playerPoints);
		}
		
		int greenbar = 0;
		if (getAchievementLevelSum(player, category) > 0)
		{
			greenbar = (248/* BAR_MAX */ * ((getAchievementLevelSum(player, category) * 100) / cat.getAchievements().size())) / 100;
			greenbar = Math.min(greenbar, 248/* BAR_MAX */);
		}
		String fp = FULL_PAGE;
		fp = fp.replaceAll("%bar1up%", "" + greenbar);
		fp = fp.replaceAll("%bar2up%", "" + (248 - greenbar));
		
		fp = fp.replaceFirst("%caps1%", greenbar > 0 ? "Gauge_DF_Large_Food_Left" : "Gauge_DF_Large_Exp_bg_Left");
		
		fp = fp.replaceFirst("%caps2%", greenbar >= 248 ? "Gauge_DF_Large_Food_Right" : "Gauge_DF_Large_Exp_bg_Right");
		
		fp = fp.replaceFirst("%achievements%", achievementsHTML.isEmpty() ? "&nbsp;" : achievementsHTML);
		fp = fp.replaceFirst("%catName%", cat.getName());
		fp = fp.replaceFirst("%catDesc%", cat.getDesc());
		fp = fp.replaceFirst("%catIcon%", cat.getIcon());
		
		player.sendPacket(new TutorialShowHtml(fp));
	}
	
	public void checkAchievementRewards(Player player)
	{
		synchronized (player.getAchievements())
		{
			for (Entry<Integer, Integer> arco : player.getAchievements().entrySet())
			{
				int achievementId = arco.getKey();
				int achievementLevel = arco.getValue();
				if (getMaxLevel(achievementId) <= achievementLevel)
				{
					continue;
				}
				
				Achievement nextLevelAchievement;
				do
				{
					achievementLevel++;
					nextLevelAchievement = getAchievement(achievementId, achievementLevel);
					if ((nextLevelAchievement != null) && nextLevelAchievement.isDone(player.getCounters().getPoints(nextLevelAchievement.getType())))
					{
						nextLevelAchievement.reward(player);
					}
				}
				while (nextLevelAchievement != null);
			}
		}
	}
	
	public int getPointsForThisLevel(int totalPoints, int achievementId, int achievementLevel)
	{
		if (totalPoints == 0)
		{
			return 0;
		}
		
		int result = 0;
		for (int i = achievementLevel; i > 0; i--)
		{
			Achievement a = getAchievement(achievementId, i);
			if (a != null)
			{
				result += a.getPointsToComplete();
			}
		}
		
		return totalPoints - result;
	}
	
	public Achievement getAchievement(int achievementId, int achievementLevel)
	{
		for (AchievementCategory cat : _achievementCategories)
		{
			for (Achievement ach : cat.getAchievements())
			{
				if ((ach.getId() == achievementId) && (ach.getLevel() == achievementLevel))
				{
					return ach;
				}
			}
		}
		
		return null;
	}
	
	public Collection<Integer> getAchievementIds()
	{
		return _achievementMaxLevels.keySet();
	}
	
	public int getMaxLevel(int id)
	{
		return _achievementMaxLevels.getOrDefault(id, 0);
	}
	
	public static int getAchievementLevelSum(Player player, int categoryId)
	{
		return player.getAchievements(categoryId).values().stream().mapToInt(level -> level).sum();
	}
	
	@Override
	public void load()
	{
		_achievementMaxLevels.clear();
		_achievementCategories.clear();
		
		parseFile(Config.findNonCustomResource("config/mod/achievements.xml"));
		
		_log.info("Achievement System: Loaded " + _achievementCategories.size() + " achievement categories and " + _achievementMaxLevels.size() + " achievements.");
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
			forEach(listNode, "categories", categoriesNode ->
			{
				forEach(categoriesNode, "cat", catNode ->
				{
					StatsSet set = parseAttributes(catNode);
					int categoryId = set.getInteger("id");
					String categoryName = set.getString("name");
					String categoryIcon = set.getString("icon");
					String categoryDesc = set.getString("desc");
					_achievementCategories.add(new AchievementCategory(categoryId, categoryName, categoryIcon, categoryDesc));
				});
			});
			
			forEach(listNode, "achievement", achievementNode ->
			{
				StatsSet set = parseAttributes(achievementNode);
				int achievementId = set.getInteger("id");
				int achievementCategory = set.getInteger("cat");
				String desc = set.getString("desc");
				String fieldType = set.getString("type");
				int[] achievementMaxLevel = { 0 };
				
				forEach(achievementNode, "level", levelNode ->
				{
					StatsSet levelSet = parseAttributes(levelNode);
					int level = levelSet.getInteger("id");
					long pointsToComplete = levelSet.getLong("need");
					int fame = levelSet.getInteger("fame");
					String name = levelSet.getString("name");
					String icon = levelSet.getString("icon");
					Achievement achievement = new Achievement(achievementId, level, name, achievementCategory, icon, desc, pointsToComplete, fieldType, fame);
					
					if (achievementMaxLevel[0] < level)
					{
						achievementMaxLevel[0] = level;
					}
					
					forEach(levelNode, "reward", rewardNode ->
					{
						StatsSet rewardSet = parseAttributes(rewardNode);
						int Itemid = rewardSet.getInteger("id");
						long Itemcount = rewardSet.getLong("count");
						achievement.addReward(Itemid, Itemcount);
					});
					
					AchievementCategory lastCategory = _achievementCategories.stream().filter(ctg -> ctg.getCategoryId() == achievementCategory).findAny().orElse(null);
					if (lastCategory != null)
					{
						lastCategory.getAchievements().add(achievement);
					}
				});
				
				_achievementMaxLevels.put(achievementId, achievementMaxLevel[0]);
			});
		});
	}
	
	public static Achievements getInstance()
	{
		if (_instance == null)
		{
			_instance = new Achievements();
		}
		return _instance;
	}
}
