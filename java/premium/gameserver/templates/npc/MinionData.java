package premium.gameserver.templates.npc;

public class MinionData
{
	
	/** The Identifier of the L2Minion */
	private final int _minionId;
	
	/** The number of this Minion Type to spawn */
	private final int _minionAmount;
	
	public MinionData(int minionId, int minionAmount)
	{
		_minionId = minionId;
		_minionAmount = minionAmount;
	}
 
	public int getMinionId()
	{
		return _minionId;
	}
	
 
	public int getAmount()
	{
		return _minionAmount;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if ((o == null) || (o.getClass() != this.getClass()))
		{
			return false;
		}
		return ((MinionData) o).getMinionId() == getMinionId();
	}
}