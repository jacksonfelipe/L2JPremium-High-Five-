package premium.gameserver.listener.inventory;

import premium.commons.listener.Listener;
import premium.gameserver.model.Playable;
import premium.gameserver.model.items.ItemInstance;

public interface OnEquipListener extends Listener<Playable>
{
	public void onEquip(int slot, ItemInstance item, Playable actor);
	
	public void onUnequip(int slot, ItemInstance item, Playable actor);
}
