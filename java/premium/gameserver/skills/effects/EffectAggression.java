package premium.gameserver.skills.effects;

import premium.gameserver.ai.PlayerAI;
import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public class EffectAggression extends Effect
{
	public EffectAggression(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectAggression(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isPlayer() && _effected != _effector)
		{
			((PlayerAI) _effected.getAI()).lockTarget(_effector);
		}
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		if (_effected.isPlayer() && _effected != _effector)
		{
			((PlayerAI) _effected.getAI()).lockTarget(null);
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}