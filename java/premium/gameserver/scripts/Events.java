package premium.gameserver.scripts;

import premium.gameserver.model.GameObject;
import premium.gameserver.model.Player;
import premium.gameserver.scripts.Scripts.ScriptClassAndMethod;
import premium.gameserver.utils.Strings;

public final class Events
{
	public static boolean onAction(Player player, GameObject obj, boolean shift)
	{
		if (shift)
		{
			if (player.getVarB("noShift"))
			{
				return false;
			}
			ScriptClassAndMethod handler = Scripts.onActionShift.get(obj.getL2ClassShortName());
			if ((handler == null) && obj.isNpc())
			{
				handler = Scripts.onActionShift.get("NpcInstance");
			}
			if ((handler == null) && obj.isPet())
			{
				handler = Scripts.onActionShift.get("PetInstance");
			}
			if (handler == null)
			{
				return false;
			}
			return Strings.parseBoolean(Scripts.getInstance().callScripts(player, handler.className, handler.methodName, new Object[]
			{
				player,
				obj
			}));
		}
		ScriptClassAndMethod handler = Scripts.onAction.get(obj.getL2ClassShortName());
		if ((handler == null) && obj.isDoor())
		{
			handler = Scripts.onAction.get("DoorInstance");
		}
		if (handler == null)
		{
			return false;
		}
		return Strings.parseBoolean(Scripts.getInstance().callScripts(player, handler.className, handler.methodName, new Object[]
		{
			player,
			obj
		}));
	}
}