package premium.gameserver.stats.conditions;

import premium.gameserver.model.entity.Reflection;
import premium.gameserver.stats.Env;

/**
 * @author VISTALL
 */
public class ConditionPlayerInstanceZone extends Condition
{
	private final int _id;
	
	public ConditionPlayerInstanceZone(int id)
	{
		_id = id;
	}
	
	@Override
	protected boolean testImpl(Env env)
	{
		Reflection ref = env.character.getReflection();
		
		return ref.getInstancedZoneId() == _id;
	}
}