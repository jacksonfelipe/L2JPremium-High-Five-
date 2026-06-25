package premium.gameserver.model.instances;

import premium.gameserver.templates.npc.NpcTemplate;

public class NpcNotSayInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public NpcNotSayInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
		setHasChatWindow(false);
	}
}
