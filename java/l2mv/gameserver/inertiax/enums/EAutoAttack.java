package l2mv.gameserver.inertiax.enums;

public enum EAutoAttack
{
	Mage,
	Melee,
	Long_Range;
	
	@Override
	public String toString()
	{
		return super.toString().replace('_', ' ');
	}

}
