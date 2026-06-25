package premium.gameserver.model.instances;

import premium.gameserver.model.Creature;
import premium.gameserver.templates.npc.NpcTemplate;

public class XmassTreeInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public XmassTreeInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isAttackable(Creature attacker)
	{
		return false;
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}
	
	@Override
	public boolean hasRandomWalk()
	{
		return false;
	}
	
	@Override
	public boolean isFearImmune()
	{
		return true;
	}
	
	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}
	
	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}