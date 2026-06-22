package premium.gameserver.data.xml;

import premium.gameserver.data.StringHolder;
import premium.gameserver.data.htm.HtmCache;
import premium.gameserver.data.xml.holder.BuyListHolder;
import premium.gameserver.data.xml.holder.MultiSellHolder;
import premium.gameserver.data.xml.holder.ProductHolder;
import premium.gameserver.data.xml.holder.RecipeHolder;
import premium.gameserver.data.xml.parser.AirshipDockParser;
import premium.gameserver.data.xml.parser.ArmorSetsParser;
import premium.gameserver.data.xml.parser.AugmentationDataParser;
import premium.gameserver.data.xml.parser.CharTemplateParser;
import premium.gameserver.data.xml.parser.CubicParser;
import premium.gameserver.data.xml.parser.DomainParser;
import premium.gameserver.data.xml.parser.DonationParse;
import premium.gameserver.data.xml.parser.DoorParser;
import premium.gameserver.data.xml.parser.DressArmorParser;
import premium.gameserver.data.xml.parser.DressCloakParser;
import premium.gameserver.data.xml.parser.DressShieldParser;
import premium.gameserver.data.xml.parser.DressWeaponParser;
import premium.gameserver.data.xml.parser.EnchantItemParser;
import premium.gameserver.data.xml.parser.EventParser;
import premium.gameserver.data.xml.parser.ExchangeItemParser;
import premium.gameserver.data.xml.parser.FacebookCommentsParser;
import premium.gameserver.data.xml.parser.FakePlayerNpcsParser;
import premium.gameserver.data.xml.parser.FightClubMapParser;
import premium.gameserver.data.xml.parser.FoundationParser;
import premium.gameserver.data.xml.parser.HennaParser;
import premium.gameserver.data.xml.parser.InstantZoneParser;
import premium.gameserver.data.xml.parser.ItemParser;
import premium.gameserver.data.xml.parser.NpcParser;
import premium.gameserver.data.xml.parser.OptionDataParser;
import premium.gameserver.data.xml.parser.PetitionGroupParser;
import premium.gameserver.data.xml.parser.PremiumParser;
import premium.gameserver.data.xml.parser.ResidenceParser;
import premium.gameserver.data.xml.parser.RestartPointParser;
import premium.gameserver.data.xml.parser.SkillAcquireParser;
import premium.gameserver.data.xml.parser.SoulCrystalParser;
import premium.gameserver.data.xml.parser.SpawnParser;
import premium.gameserver.data.xml.parser.StaticObjectParser;
import premium.gameserver.data.xml.parser.TournamentMapParser;
import premium.gameserver.data.xml.parser.ZoneParser;
import premium.gameserver.instancemanager.ReflectionManager;
import premium.gameserver.tables.SkillTable;
import premium.gameserver.tables.SpawnTable;

public abstract class Parsers
{
	public static void parseAll()
	{
		// if ((!Config.EXTERNAL_HOSTNAME.equalsIgnoreCase("127.0.0.1")) && (!Config.EXTERNAL_HOSTNAME.equalsIgnoreCase("178.33.90.147")))
		// {
		// return;
		// }
		HtmCache.getInstance().reload();
		StringHolder.getInstance().load();
		//
		SkillTable.getInstance().load(); // - SkillParser.getInstance();
		OptionDataParser.getInstance().load();
		ItemParser.getInstance().load();
		//
		FakePlayerNpcsParser.getInstance().load();
		NpcParser.getInstance().load();
		
		DomainParser.getInstance().load();
		RestartPointParser.getInstance().load();
		ExchangeItemParser.getInstance().load();
		StaticObjectParser.getInstance().load();
		DoorParser.getInstance().load();
		ZoneParser.getInstance().load();
		SpawnTable.getInstance();
		SpawnParser.getInstance().load();
		InstantZoneParser.getInstance().load();
		
		ReflectionManager.getInstance();
		
		//
		AirshipDockParser.getInstance().load();
		SkillAcquireParser.getInstance().load();
		//
		CharTemplateParser.getInstance().load();
		//
		ResidenceParser.getInstance().load();
		EventParser.getInstance().load();
		FightClubMapParser.getInstance().load();
		// support(cubic & agathion)
		CubicParser.getInstance().load();
		//
		BuyListHolder.getInstance();
		RecipeHolder.getInstance();
		MultiSellHolder.getInstance();
		ProductHolder.getInstance();
		// AgathionParser.getInstance();
		// item support
		HennaParser.getInstance().load();
		EnchantItemParser.getInstance().load();
		SoulCrystalParser.getInstance().load();
		ArmorSetsParser.getInstance().load();
		
		// etc
		PetitionGroupParser.getInstance().load();
		DressArmorParser.getInstance().load();
		DressCloakParser.getInstance().load();
		DressShieldParser.getInstance().load();
		DressWeaponParser.getInstance().load();
		AugmentationDataParser.getInstance().load();
		
		// Premium
		PremiumParser.getInstance().load();
		
		// Community Board Adds
		FoundationParser.getInstance().load();
		DonationParse.getInstance().load();
		
		// New
		TournamentMapParser.getInstance().load();
		FacebookCommentsParser.getInstance().load();
	}
}
