package premium.gameserver.network.serverpackets;

import premium.gameserver.model.base.TeamType;
import premium.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import premium.gameserver.model.entity.events.objects.UCTeamObject;

/**
 * @author VISTALL
 */
public class ExPVPMatchUserDie extends L2GameServerPacket
{
	private int _blueKills, _redKills;
	
	public ExPVPMatchUserDie(UndergroundColiseumBattleEvent e)
	{
		UCTeamObject team = e.getFirstObject(TeamType.BLUE.name());
		this._blueKills = team.getKills();
		team = e.getFirstObject(TeamType.RED.name());
		this._redKills = team.getKills();
	}
	
	public ExPVPMatchUserDie(int blueKills, int redKills)
	{
		this._blueKills = blueKills;
		this._redKills = redKills;
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x7F);
		this.writeD(this._blueKills);
		this.writeD(this._redKills);
	}
}