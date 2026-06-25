package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.PetInstance;
import premium.gameserver.model.items.ItemInstance;
import premium.gameserver.model.items.PcInventory;
import premium.gameserver.model.items.PetInventory;
import premium.gameserver.network.serverpackets.components.SystemMsg;

public class RequestGetItemFromPet extends L2GameClientPacket
{
 
	private int _objectId;
	private long _amount;
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this._amount = this.readQ();
		this._unknown = this.readD(); // = 0 for most trades
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this._amount < 1)
		{
			return;
		}
		
		if (!(activeChar.getPet() instanceof PetInstance))
		{
			activeChar.sendActionFailed();
			return;
		}
		
		PetInstance pet = (PetInstance) activeChar.getPet();
		if ((pet == null) || activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		if (activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}
		
		PetInventory petInventory = pet.getInventory();
		PcInventory playerInventory = activeChar.getInventory();
		
		ItemInstance item = petInventory.getItemByObjectId(this._objectId);
		if (item == null || item.getCount() < this._amount || item.isEquipped())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		int slots = 0;
		long weight = item.getTemplate().getWeight() * this._amount;
		if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null)
		{
			slots = 1;
		}
		
		if (!activeChar.getInventory().validateWeight(weight))
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}
		
		if (!activeChar.getInventory().validateCapacity(slots))
		{
			activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return;
		}
		
		playerInventory.addItem(petInventory.removeItemByObjectId(this._objectId, this._amount, "Pet " + activeChar.toString(), "GetItemFromPet"), "GiveItemToPet");
		
		pet.sendChanges();
		activeChar.sendChanges();
	}
}