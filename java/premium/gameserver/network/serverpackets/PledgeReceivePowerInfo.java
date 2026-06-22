package premium.gameserver.network.serverpackets;

import premium.gameserver.model.pledge.Clan;
import premium.gameserver.model.pledge.RankPrivs;
import premium.gameserver.model.pledge.UnitMember;

public class PledgeReceivePowerInfo extends L2GameServerPacket
{
	private int PowerGrade, privs;
	private String member_name;
	
	public PledgeReceivePowerInfo(UnitMember member)
	{
		this.PowerGrade = member.getPowerGrade();
		this.member_name = member.getName();
		if (member.isClanLeader())
		{
			this.privs = Clan.CP_ALL;
		}
		else
		{
			RankPrivs temp = member.getClan().getRankPrivs(member.getPowerGrade());
			if (temp != null)
			{
				this.privs = temp.getPrivs();
			}
			else
			{
				this.privs = 0;
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x3d);
		this.writeD(this.PowerGrade);
		this.writeS(this.member_name);
		this.writeD(this.privs);
	}
}