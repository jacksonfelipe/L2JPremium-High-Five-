package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.model.Summon;
import premium.gameserver.stats.Env;

public final class EffectDestroySummon extends Effect
{
	public EffectDestroySummon(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectDestroySummon(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public boolean checkCondition()
	{
		if (!_effected.isSummon())
		{
			return false;
		}
		return super.checkCondition();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		((Summon) _effected).unSummon();
	}
	
	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}