package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.model.Player;
import premium.gameserver.stats.Env;

public final class EffectVitalityStop extends Effect
{
	public EffectVitalityStop(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectVitalityStop(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		Player player = _effected.getPlayer();
		player.VitalityStop(true);
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		Player player = _effected.getPlayer();
		player.VitalityStop(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}