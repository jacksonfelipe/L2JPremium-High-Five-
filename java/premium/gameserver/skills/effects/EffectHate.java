package premium.gameserver.skills.effects;

import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public class EffectHate extends Effect
{
	public EffectHate(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectHate(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isMonster())
		{
			_effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _effector, _template._value);
		}
		// On players it makes attack the caster
		else if (_effected.isPlayable() && _effected.isMonster())
		{
			getEffected().abortAttack(true, false);
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getEffector());
		}
	}
	
	@Override
	public boolean isHidden()
	{
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}