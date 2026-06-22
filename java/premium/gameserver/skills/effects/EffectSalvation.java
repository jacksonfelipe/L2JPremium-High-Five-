package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public final class EffectSalvation extends Effect
{
	public EffectSalvation(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectSalvation(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public boolean checkCondition()
	{
		return getEffected().isPlayer() && super.checkCondition();
	}
	
	@Override
	public void onStart()
	{
		getEffected().setIsSalvation(true);
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().setIsSalvation(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}