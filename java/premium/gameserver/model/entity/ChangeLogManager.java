package premium.gameserver.model.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import premium.gameserver.Config;
import premium.gameserver.data.htm.HtmCache;
import premium.gameserver.hwid.HwidEngine;
import premium.gameserver.hwid.HwidGamer;
import premium.gameserver.model.Player;
import premium.gameserver.utils.Language;

import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

public class ChangeLogManager extends DocumentParser
{
	private static final Logger LOG = LoggerFactory.getLogger(ChangeLogManager.class);
	private final List<Change> changeList;
	
	private enum FixType
	{
		BUG,
		NEW_FEATURE,
		IMPROVEMENT
	}
	
	public ChangeLogManager()
	{
		changeList = new LinkedList<>();
		load();
	}
	
	public int getNotSeenChangeLog(Player player)
	{
		if (Config.ALLOW_HWID_ENGINE)
		{
			HwidGamer gamer = HwidEngine.getInstance().getGamerByHwid(player.getHWID());
			if (gamer != null)
			{
				int lastSeen = gamer.getSeenChangeLog();
				if (lastSeen < getLatestChangeId())
				{
					return getLatestChangeId();
				}
			}
		}
		return -1;
	}
	
	public int getLatestChangeId()
	{
		return changeList.size() - 1;
	}
	
	public String getChangeLog(int index)
	{
		Change change = changeList.get(index);
		
		StringBuilder fixesBuilder = new StringBuilder();
		for (Fix singleFix : change.getFixes())
		{
			fixesBuilder.append("<table width=280>");
			fixesBuilder.append("<tr><td align=left><font color=\"").append(getTextColor(singleFix.getType())).append("\"> - ");
			fixesBuilder.append(singleFix.getDesc());
			fixesBuilder.append("</font></td></tr></table>");
		}
		
		StringBuilder pagesBuilder = new StringBuilder();
		pagesBuilder.append("<table><tr>");
		if (index > 0)
		{
			pagesBuilder.append("<td><button value=\"Previous\" action=\"bypass -h ShowChangeLogPage ").append(index - 1).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		}
		if (index < getLatestChangeId())
		{
			pagesBuilder.append("<td><button value=\"Next\" action=\"bypass -h ShowChangeLogPage ").append(index + 1).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		}
		
		pagesBuilder.append("</tr></table>");
		
		String html = HtmCache.getInstance().getNotNull("command/changeLog.htm", Language.ENGLISH);
		html = html.replace("%date%", change.getDate());
		html = html.replace("%fixes%", fixesBuilder.toString());
		html = html.replace("%leftPageBtn%", index > 0 ? "<button value=\"Previous\" action=\"bypass -h ShowChangeLogPage " + (index - 1) + "\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">" : "<br>");
		html = html.replace("%rightPageBtn%", index < getLatestChangeId() ? "<button value=\"Next\" action=\"bypass -h ShowChangeLogPage " + (index + 1) + "\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">" : "<br>");
		return html;
	}
	
	private static String getTextColor(FixType type)
	{
		switch (type)
		{
			case BUG:
				return "9b2626";
			case NEW_FEATURE:
				return "30b33a";
			case IMPROVEMENT:
				return "b5b71f";
		}
		return "ffffff";
	}
	
	private static class Change
	{
		private final int index;
		private final String date;
		private final List<Fix> fixes;
		
		protected Change(int index, String date)
		{
			this.index = index;
			this.date = date;
			fixes = new ArrayList<>();
		}
		
		@SuppressWarnings("unused")
		public int getIndex()
		{
			return index;
		}
		
		public String getDate()
		{
			return date;
		}
		
		public void addFix(Fix fix)
		{
			fixes.add(fix);
		}
		
		public List<Fix> getFixes()
		{
			return fixes;
		}
	}
	
	private static class Fix
	{
		private final FixType type;
		private final String desc;
		
		protected Fix(FixType type, String desc)
		{
			this.type = type;
			this.desc = desc;
		}
		
		protected FixType getType()
		{
			return type;
		}
		
		protected String getDesc()
		{
			return desc;
		}
	}
	
	public void reloadChangeLog()
	{
		load();
	}
	
	@Override
	public void load()
	{
		changeList.clear();
		parseDatapackFile("data/changeLog.xml");
		Collections.reverse(changeList);
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		int[] index = { 0 };
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "change", changeNode ->
			{
				StatsSet changeSet = parseAttributes(changeNode);
				Change change = new Change(index[0]++, changeSet.getString("date"));
				
				forEach(changeNode, "fix", fixNode ->
				{
					StatsSet fixSet = parseAttributes(fixNode);
					FixType realType = FixType.valueOf(fixSet.getString("type"));
					String desc = fixSet.getString("desc");
					change.addFix(new Fix(realType, desc));
				});
				
				changeList.add(change);
			});
		});
	}
	
	public static ChangeLogManager getInstance()
	{
		return ChangeLogManagerHolder.instance;
	}
	
	private static class ChangeLogManagerHolder
	{
		protected static final ChangeLogManager instance = new ChangeLogManager();
	}
}
