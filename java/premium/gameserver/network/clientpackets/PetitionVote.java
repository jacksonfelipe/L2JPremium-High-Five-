package premium.gameserver.network.clientpackets;

/**
 * format: ddS
 */
public class PetitionVote extends L2GameClientPacket
{
	private int _type, _unk1;
	private String _petitionText;
	
	@Override
	protected void runImpl()
	{
	}
	
	@Override
	protected void readImpl()
	{
		this.settype(this.readD());
		this.setunk1(this.readD());
		this.setpetitionText(this.readS(4096));
	}

	public int gettype()
	{
		return _type;
	}

	public void settype(int _type)
	{
		this._type = _type;
	}

	public String getpetitionText()
	{
		return _petitionText;
	}

	public void setpetitionText(String _petitionText)
	{
		this._petitionText = _petitionText;
	}

	public int getunk1()
	{
		return _unk1;
	}

	public void setunk1(int _unk1)
	{
		this._unk1 = _unk1;
	}
}