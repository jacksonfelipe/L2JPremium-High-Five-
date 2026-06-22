package premium.gameserver.network.serverpackets;

import java.util.Collection;

import premium.gameserver.data.xml.holder.PetitionGroupHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.petition.PetitionMainGroup;
import premium.gameserver.utils.Language;

/**
 * @author VISTALL
 */
public class ExResponseShowStepOne extends L2GameServerPacket
{
	private Language _language;
	
	public ExResponseShowStepOne(Player player)
	{
		this._language = player.getLanguage();
	}
	
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xAE);
		Collection<PetitionMainGroup> petitionGroups = PetitionGroupHolder.getInstance().getPetitionGroups();
		this.writeD(petitionGroups.size());
		for (PetitionMainGroup group : petitionGroups)
		{
			this.writeC(group.getId());
			this.writeS(group.getName(this._language));
		}
	}
}