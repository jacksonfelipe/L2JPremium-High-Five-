package premium.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.gameserver.instancemanager.games.HandysBlockCheckerManager;
import premium.gameserver.model.Player;

/**
 * Format: chdd d: team
 */
public final class RequestExCubeGameChangeTeam extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestExCubeGameChangeTeam.class);
	
	int _team, _arena;
	
	@Override
	protected void readImpl()
	{
		this._arena = this.readD() + 1;
		this._team = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (HandysBlockCheckerManager.arenaIsBeingUsed(this._arena))
		{
			return;
		}
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || activeChar.isDead())
		{
			return;
		}
		
		switch (this._team)
		{
			case 0:
			case 1:
				// Change Player Team
				HandysBlockCheckerManager.changePlayerToTeam(activeChar, this._arena, this._team);
				break;
			case -1:
			{
				int team = HandysBlockCheckerManager.getHolder(this._arena).getPlayerTeam(activeChar);
				// client sends two times this packet if click on exit
				// client did not send this packet on restart
				if (team > -1)
				{
					HandysBlockCheckerManager.getInstance().removePlayer(activeChar, this._arena, team);
				}
				break;
			}
			default:
				_log.warn("Wrong Team ID: " + this._team);
				break;
		}
	}
}
