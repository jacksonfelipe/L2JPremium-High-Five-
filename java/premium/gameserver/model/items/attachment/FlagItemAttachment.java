package premium.gameserver.model.items.attachment;

import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;

public interface FlagItemAttachment extends PickableAttachment
{
	// FIXME [claww] may alter the listener Player
	void onLogout(Player player);
	
	// FIXME [claww] may alter the listener Player
	void onDeath(Player owner, Creature killer);
	
	void onOutTerritory(Player player);
	
	boolean canAttack(Player player);
	
	boolean canCast(Player player, Skill skill);
	
	boolean canBeLost();
	
	boolean canBeUnEquiped();
}
