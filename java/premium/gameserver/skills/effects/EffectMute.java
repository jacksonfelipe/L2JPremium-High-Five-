package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.model.Skill;
import premium.gameserver.stats.Env;

public class EffectMute extends Effect
{
	public EffectMute(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectMute(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		if (!_effected.startMuted())
		{
			Skill castingSkill = _effected.getCastingSkill();
			if (castingSkill != null && castingSkill.isMagic())
			{
				_effected.abortCast(true, true);
			}
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopMuted();
	}
}