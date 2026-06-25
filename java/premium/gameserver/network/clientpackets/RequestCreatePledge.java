package premium.gameserver.network.clientpackets;

public class RequestCreatePledge extends L2GameClientPacket
{
	// Format: cS
	public String _pledgename;
	
	@Override
	protected void readImpl()
	{
		this._pledgename = this.readS(64);
	}
	
	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}