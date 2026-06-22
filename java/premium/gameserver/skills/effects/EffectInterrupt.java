package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public class EffectInterrupt extends Effect
{
	public EffectInterrupt(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectInterrupt(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (!getEffected().isRaid())
		{
			getEffected().abortCast(true, true);
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}