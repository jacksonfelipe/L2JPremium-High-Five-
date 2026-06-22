package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.skills.skillclasses.NegateStats;
import premium.gameserver.stats.Env;

public class EffectBlockStat extends Effect
{
	public EffectBlockStat(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectBlockStat(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		_effected.addBlockStats(((NegateStats) _skill).getNegateStats());
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		_effected.removeBlockStats(((NegateStats) _skill).getNegateStats());
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}