package npc.model;

import premium.gameserver.instancemanager.SoDManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class AllenosInstance extends NpcInstance
{
	private static final int tiatIzId = 110;
	
	public AllenosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		
		if (command.equalsIgnoreCase("enter_seed"))
		{
			// Время открытого SoD прошло
			if (SoDManager.isAttackStage())
			{
				Reflection r = player.getActiveReflection();
				if (r != null)
				{
					if (player.canReenterInstance(tiatIzId))
					{
						player.teleToLocation(r.getTeleportLoc(), r);
					}
				}
				else if (player.canEnterInstance(tiatIzId))
				{
					ReflectionUtils.enterReflection(player, tiatIzId);
				}
			}
			else
			{
				SoDManager.teleportIntoSeed(player);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}