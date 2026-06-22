package premium.gameserver.skills.effects;

import premium.gameserver.cache.Msg;
import premium.gameserver.model.Effect;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.SystemMessage;
import premium.gameserver.stats.Env;

public class EffectVitalityDamOverTime extends Effect
{
	public EffectVitalityDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectVitalityDamOverTime(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public boolean onActionTime()
	{
		if ((_effected.isDead()) || (!_effected.isPlayer()))
		{
			return false;
		}
		Player _pEffected = (Player) _effected;
		
		double vitDam = calc();
		if ((vitDam > _pEffected.getVitality()) && (getSkill().isToggle()))
		{
			_pEffected.sendPacket(Msg.NOT_ENOUGH_MATERIALS);
			_pEffected.sendPacket(new SystemMessage(SystemMessage.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
			return false;
		}
		
		_pEffected.setVitality(Math.max(0.0D, _pEffected.getVitality() - vitDam));
		return true;
	}
}