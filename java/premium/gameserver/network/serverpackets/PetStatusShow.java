package premium.gameserver.network.serverpackets;

import premium.gameserver.model.Summon;

public class PetStatusShow extends L2GameServerPacket
{
	private int _summonType;
	
	public PetStatusShow(Summon summon)
	{
		this._summonType = summon.getSummonType();
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeC(0xb1);
		this.writeD(this._summonType);
	}
}