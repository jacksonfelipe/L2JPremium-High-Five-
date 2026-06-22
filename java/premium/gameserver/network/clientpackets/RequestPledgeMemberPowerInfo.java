package premium.gameserver.network.clientpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.pledge.Clan;
import premium.gameserver.model.pledge.UnitMember;
import premium.gameserver.network.serverpackets.PledgeReceivePowerInfo;

public class RequestPledgeMemberPowerInfo extends L2GameClientPacket
{
	// format: chdS
	@SuppressWarnings("unused")
	private int _not_known;
	private String _target;
	
	@Override
	protected void readImpl()
	{
		this._not_known = this.readD();
		this._target = this.readS(16);
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		Clan clan = activeChar.getClan();
		if (clan != null)
		{
			UnitMember cm = clan.getAnyMember(this._target);
			if (cm != null)
			{
				activeChar.sendPacket(new PledgeReceivePowerInfo(cm));
			}
		}
	}
}