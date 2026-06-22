package npc.model;

import premium.gameserver.Config;
import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.ActionFail;
import premium.gameserver.network.serverpackets.MyTargetSelected;
import premium.gameserver.network.serverpackets.StatusUpdate;
import premium.gameserver.network.serverpackets.ValidateLocation;
import premium.gameserver.scripts.Events;
import premium.gameserver.templates.npc.NpcTemplate;

public final class BossRespawnInstance extends NpcInstance
{
	
	private static final long serialVersionUID = 1L;
	
	public BossRespawnInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onAction(Player player, boolean shift)
	{
		if (!isTargetable())
		{
			player.sendActionFailed();
			return;
		}
		
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			if (player.getTarget() == this)
			{
				player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()), makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
			}
			
			player.sendPacket(new ValidateLocation(this), ActionFail.STATIC);
			return;
		}
		
		if (Events.onAction(player, this, shift))
		{
			player.sendActionFailed();
			return;
		}
		
		// Synerge - Send event to fight club to see if the event handles this npc selection
		if (player.isInFightClub())
		{
			if (player.getFightClubEvent().onTalkNpc(player, this))
			{
				player.sendActionFailed();
				return;
			}
		}
		
		if (isAutoAttackable(player))
		{
			player.getAI().Attack(this, false, shift);
			return;
		}
		
		if (!isInRangeZ(player, INTERACTION_DISTANCE)) // Nik: Changed to isInRangeZ because players can exploit it like waking Baium from TOI 13
		{
			if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			}
			return;
		}
		
		if (!Config.ALLOW_TALK_TO_NPCS)
		{
			player.sendActionFailed();
			return;
		}
		
		if ((!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting()) || player.isAlikeDead())
		{
			return;
		}
		
		if (hasRandomAnimation())
		{
			onRandomAnimation();
		}
		
		player.sendActionFailed();
		player.stopMove(false);
		
	}
	
}