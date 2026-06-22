package premium.gameserver.skills.effects;

import premium.commons.util.Rnd;
import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public final class EffectStun extends Effect
{
	public EffectStun(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectStun(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public boolean checkCondition()
	{
		return Rnd.chance(_template.chance(80));
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startStunning();
		_effected.abortAttack(true, true);
		_effected.abortCast(true, true);
		_effected.stopMove();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopStunning();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}