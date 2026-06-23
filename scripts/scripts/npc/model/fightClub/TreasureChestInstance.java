package npc.model.fightClub;

import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.events.impl.fightclub.FFATreasureHuntEvent;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.ActionFail;
import premium.gameserver.network.serverpackets.MyTargetSelected;
import premium.gameserver.network.serverpackets.StatusUpdate;
import premium.gameserver.network.serverpackets.ValidateLocation;
import premium.gameserver.templates.npc.NpcTemplate;

public class TreasureChestInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public TreasureChestInstance(int objectId, NpcTemplate template)
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
		
		if (player.getTarget() == null || !player.getTarget().equals(this))
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()), makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
			player.sendPacket(new ValidateLocation(this), ActionFail.STATIC);
			return;
		}
		
		if (!isInRange(player, INTERACTION_DISTANCE))
		{
			if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			}
			return;
		}
		
		if (player.isSitting() || player.isAlikeDead())
		{
			return;
		}
		
		player.sendActionFailed();
		player.stopMove(false);
		
		if (player.isInFightClub())
		{
			boolean shouldDisappear = false;
			if (player.getFightClubEvent() instanceof FFATreasureHuntEvent)
			{
				shouldDisappear = ((FFATreasureHuntEvent) player.getFightClubEvent()).openTreasure(player, this);
			}
			
			if (shouldDisappear)
			{
				deleteMe();
			}
		}
	}
}
