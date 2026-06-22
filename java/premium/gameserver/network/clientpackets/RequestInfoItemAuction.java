package premium.gameserver.network.clientpackets;

import premium.gameserver.instancemanager.itemauction.ItemAuction;
import premium.gameserver.instancemanager.itemauction.ItemAuctionInstance;
import premium.gameserver.instancemanager.itemauction.ItemAuctionManager;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.ExItemAuctionInfo;

/**
 * @author n0nam3
 */
public final class RequestInfoItemAuction extends L2GameClientPacket
{
	private int _instanceId;
	
	@Override
	protected final void readImpl()
	{
		this._instanceId = this.readD();
	}
	
	@Override
	protected final void runImpl()
	{
		final Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.getAndSetLastItemAuctionRequest();
		
		final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(this._instanceId);
		if (instance == null)
		{
			return;
		}
		
		final ItemAuction auction = instance.getCurrentAuction();
		NpcInstance broker = activeChar.getLastNpc();
		if (auction == null || broker == null || broker.getNpcId() != this._instanceId || activeChar.getDistance(broker.getX(), broker.getY()) > Creature.INTERACTION_DISTANCE)
		{
			return;
		}
		
		activeChar.sendPacket(new ExItemAuctionInfo(true, auction, instance.getNextAuction()));
	}
}