package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.network.serverpackets.FinishRotating;
import premium.gameserver.network.serverpackets.StartRotating;
import premium.gameserver.stats.Env;

public final class EffectBluff extends Effect
{
	public EffectBluff(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectBluff(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public boolean checkCondition()
	{
		if (getEffected().isNpc() && !getEffected().isMonster())
		{
			return false;
		}
		return super.checkCondition();
	}
	
	@Override
	public void onStart()
	{
		getEffected().broadcastPacket(new StartRotating(getEffected(), getEffected().getHeading(), 1, 65535));
		getEffected().broadcastPacket(new FinishRotating(getEffected(), getEffector().getHeading(), 65535));
		getEffected().setHeading(getEffector().getHeading());
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