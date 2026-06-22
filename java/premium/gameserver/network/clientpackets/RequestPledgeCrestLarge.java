package premium.gameserver.network.clientpackets;

import premium.gameserver.cache.CrestCache;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.ExPledgeCrestLarge;

public class RequestPledgeCrestLarge extends L2GameClientPacket
{
	// format: chd
	private int _crestId;
	
	@Override
	protected void readImpl()
	{
		this._crestId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if ((activeChar == null) || (this._crestId == 0))
		{
			return;
		}
		byte[] data = CrestCache.getInstance().getPledgeCrestLarge(this._crestId);
		if (data != null)
		{
			ExPledgeCrestLarge pcl = new ExPledgeCrestLarge(this._crestId, data);
			this.sendPacket(pcl);
		}
	}
}