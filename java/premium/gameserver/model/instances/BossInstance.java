package premium.gameserver.model.instances;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.Hero;
import premium.gameserver.model.entity.HeroDiary;
import premium.gameserver.templates.npc.NpcTemplate;

public class BossInstance extends RaidBossInstance
{
	private static final long serialVersionUID = 1L;
	private boolean _teleportedToNest;
	
	public BossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isBoss()
	{
		return true;
	}
	
	@Override
	public final boolean isMovementDisabled()
	{
		// Core should stay anyway
		return getNpcId() == 29006 || super.isMovementDisabled();
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		if (killer.isPlayable())
		{
			Player player = killer.getPlayer();
			if (player.isInParty())
			{
				for (Player member : player.getParty().getMembers())
				{
					if (member.isNoble())
					{
						Hero.getInstance().addHeroDiary(member.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
					}
				}
			}
			else if (player.isNoble())
			{
				Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
			}
		}
		super.onDeath(killer);
	}
	
	/**
	 * Used by Orfen to set 'teleported' flag, when hp goes to <50%
	 * @param flag
	 */
	public void setTeleported(boolean flag)
	{
		_teleportedToNest = flag;
	}
	
	public boolean isTeleported()
	{
		return _teleportedToNest;
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}
