package premium.gameserver.model.instances;

import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.templates.npc.NpcTemplate;

public class ChestInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public ChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	public void tryOpen(Player opener, Skill skill)
	{
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, opener, 100);
	}
	
	@Override
	public boolean canChampion()
	{
		return false;
	}
}