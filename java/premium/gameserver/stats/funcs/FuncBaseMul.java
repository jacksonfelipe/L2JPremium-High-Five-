package premium.gameserver.stats.funcs;

import premium.gameserver.stats.Env;
import premium.gameserver.stats.Stats;

public class FuncBaseMul extends Func
{
	public FuncBaseMul(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner, value);
	}
	
	@Override
	public void calc(Env env)
	{
		if (cond == null || cond.test(env))
		{
			env.value += value;
		}
	}
}
