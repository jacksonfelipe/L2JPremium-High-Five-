package services;

import premium.commons.text.PrintfFormat;
import premium.gameserver.Config;
import premium.gameserver.data.xml.holder.MultiSellHolder;
import premium.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import premium.gameserver.model.base.MultiSellEntry;
import premium.gameserver.scripts.Functions;
import premium.gameserver.scripts.ScriptFile;

public class PurpleManedHorse extends Functions implements ScriptFile
{
	private static boolean Enabled = false;
	private static final int MultiSellID = -1001;
	private static final PrintfFormat dlg = new PrintfFormat("<br>[npc_%%objectId%%_Multisell %d|%s]");
	private static MultiSellListContainer list;
	
	@Override
	public void onLoad()
	{
		if (Config.SERVICES_SELLPETS.isEmpty())
		{
			return;
		}
		String[] SELLPETS = Config.SERVICES_SELLPETS.split(";");
		if (SELLPETS.length == 0)
		{
			return;
		}
		
		list = new MultiSellListContainer();
		list.setNoTax(true);
		list.setShowAll(true);
		list.setKeepEnchant(false);
		list.setNoKey(true);
		int entId = 1;
		for (String SELLPET : SELLPETS)
		{
			MultiSellEntry e = MultiSellHolder.parseEntryFromStr(SELLPET);
			if (e != null)
			{
				e.setEntryId(entId++);
				list.addEntry(e);
			}
		}
		if (list.getEntries().size() == 0)
		{
			return;
		}
		
		Enabled = true;
		loadMultiSell();
	}
	
	@Override
	public void onReload()
	{
	}
	
	@Override
	public void onShutdown()
	{
	}
	
	private static void loadMultiSell()
	{
		MultiSellHolder.getInstance().addMultiSellListContainer(MultiSellID, list);
	}
	
	public String PetManagersDialogAppend(Integer val)
	{
		if (val == 0 && Enabled)
		{
			return dlg.sprintf(MultiSellID, "Buy New Pets");
		}
		return "";
	}
	
	public String DialogAppend_30731(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_30827(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_30828(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_30829(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_30830(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_30831(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_30869(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_31067(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_31265(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_31309(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
	
	public String DialogAppend_31954(Integer val)
	{
		return PetManagersDialogAppend(val);
	}
}