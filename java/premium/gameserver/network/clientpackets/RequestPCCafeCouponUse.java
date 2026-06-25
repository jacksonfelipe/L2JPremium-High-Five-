package premium.gameserver.network.clientpackets;

/**
 * format: chS
 */
public class RequestPCCafeCouponUse extends L2GameClientPacket
{
	// format: (ch)S
	public String _unknown;
	
	@Override
	protected void readImpl()
	{
		this._unknown = this.readS();
	}
	
	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}