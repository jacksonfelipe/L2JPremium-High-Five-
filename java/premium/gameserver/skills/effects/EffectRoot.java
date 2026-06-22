package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public final class EffectRoot extends Effect
{
	public EffectRoot(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectRoot(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startRooted();
		_effected.stopMove();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopRooted();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}