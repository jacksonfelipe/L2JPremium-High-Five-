package premium.gameserver.skills.effects;

import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.cache.Msg;
import premium.gameserver.geodata.GeoEngine;
import premium.gameserver.model.Effect;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.events.impl.SiegeEvent;
import premium.gameserver.model.instances.SummonInstance;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.stats.Env;
import premium.gameserver.utils.PositionUtils;

public final class EffectDummy2 extends Effect
{
	public static final double FEAR_RANGE = 900.0D;
	
	public EffectDummy2(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectDummy2(Effect effect)
	{
		super(effect);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean checkCondition()
	{
		if (_effected.isFearImmune())
		{
			getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		
		Player player = _effected.getPlayer();
		if (player != null)
		{
			SiegeEvent siegeEvent = (SiegeEvent) player.getEvent(SiegeEvent.class);
			if ((_effected.isSummon()) && (siegeEvent != null) && (siegeEvent.containsSiegeSummon((SummonInstance) _effected)))
			{
				getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		
		if (_effected.isInZonePeace())
		{
			getEffector().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
			return false;
		}
		
		return super.checkCondition();
	}
	
	@Override
	public void onStart()
	{
		Player target = (Player) getEffected();
		if (target.getTransformation() == 303)
		{
			return;
		}
		super.onStart();
		
		if (!_effected.startFear())
		{
			_effected.abortAttack(true, true);
			_effected.abortCast(true, true);
			_effected.stopMove();
		}
		
		onActionTime();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopFear();
		_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
	
	@Override
	public boolean onActionTime()
	{
		double angle = Math.toRadians(PositionUtils.calculateAngleFrom(_effector, _effected));
		int oldX = _effected.getX();
		int oldY = _effected.getY();
		int x = oldX + (int) (900.0D * Math.cos(angle));
		int y = oldY + (int) (900.0D * Math.sin(angle));
		_effected.setRunning();
		_effected.moveToLocation(GeoEngine.moveCheck(oldX, oldY, _effected.getZ(), x, y, _effected.getGeoIndex()), 0, false);
		return true;
	}
}