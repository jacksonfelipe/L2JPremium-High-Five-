package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.stats.Env;

public class EffectMuteAll extends Effect
{
	public EffectMuteAll(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectMuteAll(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startMuted();
		_effected.startPMuted();
		_effected.abortCast(true, true);
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopMuted();
		_effected.stopPMuted();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}