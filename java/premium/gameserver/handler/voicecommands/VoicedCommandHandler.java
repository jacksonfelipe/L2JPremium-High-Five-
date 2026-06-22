package premium.gameserver.handler.voicecommands;

import java.util.HashMap;
import java.util.Map;

import premium.commons.data.xml.AbstractHolder;
import premium.gameserver.Config;
import premium.gameserver.handler.voicecommands.impl.ACP;
import premium.gameserver.handler.voicecommands.impl.AchievementsVoice;
import premium.gameserver.handler.voicecommands.impl.AntiGrief;
import premium.gameserver.handler.voicecommands.impl.Atod;
import premium.gameserver.handler.voicecommands.impl.Away;
import premium.gameserver.handler.voicecommands.impl.BuffStoreVoice;
import premium.gameserver.handler.voicecommands.impl.CWHPrivileges;
import premium.gameserver.handler.voicecommands.impl.Cfg;
import premium.gameserver.handler.voicecommands.impl.CombineTalismans;
import premium.gameserver.handler.voicecommands.impl.Debug;
//import premium.gameserver.handler.voicecommands.impl.Donate;
import premium.gameserver.handler.voicecommands.impl.DressMe;
import premium.gameserver.handler.voicecommands.impl.FacebookVoice;
import premium.gameserver.handler.voicecommands.impl.FindParty;
import premium.gameserver.handler.voicecommands.impl.Hellbound;
import premium.gameserver.handler.voicecommands.impl.Help;
import premium.gameserver.handler.voicecommands.impl.ItemLogsVoice;
import premium.gameserver.handler.voicecommands.impl.LockPc;
import premium.gameserver.handler.voicecommands.impl.NpcSpawn;
import premium.gameserver.handler.voicecommands.impl.Offline;
import premium.gameserver.handler.voicecommands.impl.Online;
import premium.gameserver.handler.voicecommands.impl.Password;
import premium.gameserver.handler.voicecommands.impl.Ping;
import premium.gameserver.handler.voicecommands.impl.PollVoice;
import premium.gameserver.handler.voicecommands.impl.Relocate;
import premium.gameserver.handler.voicecommands.impl.Repair;
import premium.gameserver.handler.voicecommands.impl.ReportBot;
import premium.gameserver.handler.voicecommands.impl.RuVotePanel;
import premium.gameserver.handler.voicecommands.impl.Security;
import premium.gameserver.handler.voicecommands.impl.ServerInfo;
import premium.gameserver.handler.voicecommands.impl.SiegeVoice;
import premium.gameserver.handler.voicecommands.impl.Teleport;
import premium.gameserver.handler.voicecommands.impl.TournamentVoice;
import premium.gameserver.handler.voicecommands.impl.VoiceGmEvent;
import premium.gameserver.handler.voicecommands.impl.VoteReward;
import premium.gameserver.handler.voicecommands.impl.Wedding;
import premium.gameserver.handler.voicecommands.impl.WhoAmI;
import premium.gameserver.handler.voicecommands.impl.res;
import premium.gameserver.handler.voicecommands.impl.BotReport.ReportCommand;
import premium.gameserver.masteriopack.rankpvpsystem.VoicedCommandHandlerPvpInfo;

public class VoicedCommandHandler extends AbstractHolder
{
	private static final VoicedCommandHandler _instance = new VoicedCommandHandler();
	
	public static VoicedCommandHandler getInstance()
	{
		return _instance;
	}
	
	private final Map<String, IVoicedCommandHandler> _datatable = new HashMap<>();
	
	private VoicedCommandHandler()
	{
		registerVoicedCommandHandler(new Away());
		registerVoicedCommandHandler(new Atod());
		registerVoicedCommandHandler(new AntiGrief());
		registerVoicedCommandHandler(new CombineTalismans());
		registerVoicedCommandHandler(new Cfg());
		registerVoicedCommandHandler(new Help());
		registerVoicedCommandHandler(new Online());
		registerVoicedCommandHandler(new Hellbound());
		registerVoicedCommandHandler(new Teleport());
		registerVoicedCommandHandler(new PollVoice());
		registerVoicedCommandHandler(new CWHPrivileges());
		registerVoicedCommandHandler(new Offline());
		registerVoicedCommandHandler(new Password());
		registerVoicedCommandHandler(new Relocate());
		registerVoicedCommandHandler(new Repair());
		registerVoicedCommandHandler(new ServerInfo());
		registerVoicedCommandHandler(new Wedding());
		registerVoicedCommandHandler(new WhoAmI());
		registerVoicedCommandHandler(new Debug());
		registerVoicedCommandHandler(new Security());
		registerVoicedCommandHandler(new ReportBot());
		registerVoicedCommandHandler(new res());
		registerVoicedCommandHandler(new FindParty());
		registerVoicedCommandHandler(new Ping());
		registerVoicedCommandHandler(new LockPc());
		registerVoicedCommandHandler(new NpcSpawn());
		// registerVoicedCommandHandler(new Donate());
		
		if (Config.ENABLE_ACHIEVEMENTS)
		{
			registerVoicedCommandHandler(new AchievementsVoice());
		}
		
		// Synerge
		registerVoicedCommandHandler(new ReportCommand());
		registerVoicedCommandHandler(new SiegeVoice());
		registerVoicedCommandHandler(new BuffStoreVoice());
		registerVoicedCommandHandler(new VoiceGmEvent());
		registerVoicedCommandHandler(new ACP());
		registerVoicedCommandHandler(new ItemLogsVoice());
		registerVoicedCommandHandler(new FacebookVoice());
		registerVoicedCommandHandler(new DressMe());
		registerVoicedCommandHandler(new VoteReward());
		registerVoicedCommandHandler(new RuVotePanel());
		registerVoicedCommandHandler(new TournamentVoice());
		registerVoicedCommandHandler(new VoicedCommandHandlerPvpInfo());
	}
	
	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for (String element : ids)
		{
			_datatable.put(element, handler);
		}
	}
	
	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		
		return _datatable.get(command);
	}
	
	@Override
	public int size()
	{
		return _datatable.size();
	}
	
	@Override
	public void clear()
	{
		_datatable.clear();
	}
}
