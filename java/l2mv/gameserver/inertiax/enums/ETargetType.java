package l2mv.gameserver.inertiax.enums;

public enum ETargetType
{
	MONSTRO,
	RAID_BOSS,
	GRAND_BOSS;

	@Override
	public String toString()
	{
		return super.toString().replace('_', ' ');
	}
}
