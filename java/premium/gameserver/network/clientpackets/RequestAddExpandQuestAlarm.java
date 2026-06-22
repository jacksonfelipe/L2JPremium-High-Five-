package premium.gameserver.network.clientpackets;

import premium.gameserver.instancemanager.QuestManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.quest.Quest;
import premium.gameserver.model.quest.QuestState;
import premium.gameserver.network.serverpackets.ExQuestNpcLogList;

/**
 * @author VISTALL
 * @date 14:47/26.02.2011
 */
public class RequestAddExpandQuestAlarm extends L2GameClientPacket
{
	private int _questId;
	
	@Override
	protected void readImpl()
	{
		this._questId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		Quest quest = QuestManager.getQuest(this._questId);
		if (quest == null)
		{
			return;
		}
		
		QuestState state = player.getQuestState(quest.getClass());
		if (state == null)
		{
			return;
		}
		
		player.sendPacket(new ExQuestNpcLogList(state));
	}
}
