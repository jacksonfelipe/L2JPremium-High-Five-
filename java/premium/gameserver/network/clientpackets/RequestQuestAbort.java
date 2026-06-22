package premium.gameserver.network.clientpackets;

import premium.gameserver.instancemanager.QuestManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.quest.Quest;
import premium.gameserver.model.quest.QuestState;

public class RequestQuestAbort extends L2GameClientPacket
{
	private int _questID;
	
	@Override
	protected void readImpl()
	{
		this._questID = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		Quest quest = QuestManager.getQuest(this._questID);
		if (activeChar == null || quest == null || activeChar.isBlocked() || !quest.canAbortByPacket())
		{
			return;
		}
		
		QuestState qs = activeChar.getQuestState(quest.getClass());
		if (qs != null && !qs.isCompleted())
		{
			qs.abortQuest();
		}
	}
}