package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public final class EffectDebuffImmunity extends Effect
{
	public EffectDebuffImmunity(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectDebuffImmunity(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startDebuffImmunity();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().stopDebuffImmunity();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}