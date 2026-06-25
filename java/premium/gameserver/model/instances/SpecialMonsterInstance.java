package premium.gameserver.model.instances;

import premium.gameserver.templates.npc.NpcTemplate;

public class SpecialMonsterInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public SpecialMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean canChampion()
	{
		return false;
	}
}