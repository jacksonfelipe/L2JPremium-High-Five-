package premium.gameserver.network.clientpackets;

import premium.gameserver.Config;
import premium.gameserver.data.xml.holder.PetitionGroupHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.petition.PetitionMainGroup;
import premium.gameserver.network.serverpackets.ExResponseShowStepTwo;

/**
 * @author VISTALL
 */
public class RequestExShowStepTwo extends L2GameClientPacket
{
	private int _petitionGroupId;
	
	@Override
	protected void readImpl()
	{
		this._petitionGroupId = this.readC();
	}
	
	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
		{
			return;
		}
		
		PetitionMainGroup group = PetitionGroupHolder.getInstance().getPetitionGroup(this._petitionGroupId);
		if (group == null)
		{
			return;
		}
		
		player.setPetitionGroup(group);
		player.sendPacket(new ExResponseShowStepTwo(player, group));
	}
}