package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public class EffectMPDamPercent extends Effect
{
	public EffectMPDamPercent(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectMPDamPercent(Effect effect)
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
		
		double newMp = (100. - calc()) * _effected.getMaxMp() / 100.;
		newMp = Math.min(_effected.getCurrentMp(), Math.max(0, newMp));
		_effected.setCurrentMp(newMp);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}