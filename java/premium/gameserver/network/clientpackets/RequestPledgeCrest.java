package premium.gameserver.network.clientpackets;

import premium.gameserver.cache.CrestCache;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.PledgeCrest;

public class RequestPledgeCrest extends L2GameClientPacket
{
	// format: cd
	
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
		byte[] data = CrestCache.getInstance().getPledgeCrest(this._crestId);
		if (data != null)
		{
			PledgeCrest pc = new PledgeCrest(this._crestId, data);
			this.sendPacket(pc);
		}
	}
}