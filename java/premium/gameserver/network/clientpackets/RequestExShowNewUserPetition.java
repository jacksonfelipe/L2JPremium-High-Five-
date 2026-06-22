package premium.gameserver.network.clientpackets;

import premium.gameserver.Config;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.ExResponseShowStepOne;

/**
 * @author VISTALL
 */
public class RequestExShowNewUserPetition extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
		{
			return;
		}
		
		player.sendPacket(new ExResponseShowStepOne(player));
	}
}