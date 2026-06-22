package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public class EffectHPDamPercent extends Effect
{
	public EffectHPDamPercent(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectHPDamPercent(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		if (_effected.isDead())
		{
			return;
		}
		
		double newHp = (100. - calc()) * _effected.getMaxHp() / 100.;
		newHp = Math.min(_effected.getCurrentHp(), Math.max(0, newHp));
		_effected.setCurrentHp(newHp, false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}