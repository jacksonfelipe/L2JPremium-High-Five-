package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.entity.Reflection;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.npc.NpcTemplate;
import premium.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class GruffManInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;
	private static final int elcardiaIzId = 158;
	
	public GruffManInstance(int objectId, NpcTemplate template)
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
		
		if (command.equalsIgnoreCase("elcardia_enter"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(elcardiaIzId))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(elcardiaIzId))
			{
				ReflectionUtils.enterReflection(player, elcardiaIzId);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}