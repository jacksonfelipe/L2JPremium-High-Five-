package l2mv.gameserver.inertiax.model.ext;

import java.util.StringTokenizer;

import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.inertiax.enums.IInertiaCmd.EInertiaEvt;
import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.InertiaAct;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.MyTargetSelected;
import l2mv.gameserver.network.serverpackets.StatusUpdate;

public class PlayerExt extends InertiaExt
{
	@Override
	protected void initExt()
	{
		register(EInertiaEvt.EVT__ATK_TARGET, this::atkTarget);
		register(EInertiaEvt.EVT__REM_TARGET, this::remTarget);
		register(EInertiaEvt.EVT__NEW_TARGET, this::newTarget);
		

		register(EInertiaEvt.EVT__CREDIT_END, this::creditsEnd);

		register(EInertiaEvt.EVT__WHILE_TARGET_DEAD, this::whileTargetDead);
		register(EInertiaEvt.EVT__WHILE_DEAD, this::whileDead);

		register(EInertiaEvt.EVT__FOLLOW_CLOSE, this::onFollowClose);
		register(EInertiaEvt.EVT__FOLLOW_FAR, this::onFollowFar);

		register(EInertiaEvt.EVT__ASSIST_NO_TARGET, this::onAssistNoTarget);

		super.initExt();
	}
	
	private EActResult atkTarget(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		
		final Creature actualTarget = inertiaAct.getTarget();
		
		if (player != null && player != actualTarget && actualTarget.isAutoAttackable(player))
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, actualTarget);
		
		return EActResult.ACT__CONTINUE;
	}
	
	private EActResult remTarget(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		
		if (player.isCastingNow())
			player.abortCast(true, true);
		if (player.isAttackingNow())
			player.abortAttack(true, true);
		player.setTarget(null);

		return EActResult.ACT__CONTINUE;
	}
	
	private EActResult newTarget(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		
		final Creature newTarget = inertiaAct.getTarget();
		
		player.setTarget(newTarget);
		if (newTarget != null)
		{
			player.sendPacket(new MyTargetSelected(newTarget.getObjectId(), 0));
			player.sendPacket(newTarget.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
		}
		
		return EActResult.ACT__CONTINUE;
	}

	private EActResult whileTargetDead(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		
		player.setTarget(null);
		
		return EActResult.ACT__CONTINUE;
	}
	
	private EActResult whileDead(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		final Inertia inertia = inertiaAct.getInertia();
		
		if (player != null && !player.isPhantom())
			inertia.setRunning(false);
		
		return EActResult.ACT__CONTINUE;
	}
	
	private EActResult creditsEnd(final InertiaAct inertiaAct)
	{
		final Inertia inertia = inertiaAct.getInertia();
		inertia.setRunning(false);
		inertia.render();
		
		return EActResult.ACT__EXIT;
	}
	
	private EActResult onFollowClose(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);	

		return EActResult.ACT__EXIT;
	}
	
	private EActResult onFollowFar(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		
		final Player assistPlayer = inertiaAct.getAssistPlayer();
		
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, assistPlayer, 100);

		return EActResult.ACT__EXIT;
	}
	
	private EActResult onAssistNoTarget(final InertiaAct inertiaAct)
	{
		final Player player = inertiaAct.getActivePlayer();
		
		final Player assistPlayer = inertiaAct.getAssistPlayer();
		
		player.setTarget(assistPlayer);
		if (assistPlayer != null)
		{
			player.sendPacket(new MyTargetSelected(assistPlayer.getObjectId(), 0));
			player.sendPacket(assistPlayer.makeStatusUpdate(StatusUpdate.MAX_HP, StatusUpdate.MAX_MP, StatusUpdate.MAX_CP, StatusUpdate.CUR_HP, StatusUpdate.CUR_MP, StatusUpdate.CUR_CP));
		}

		return EActResult.ACT__EXIT;
	}

}
