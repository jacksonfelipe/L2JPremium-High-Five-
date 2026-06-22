package premium.gameserver.network.serverpackets;

import java.util.Collection;

import premium.gameserver.model.Player;
import premium.gameserver.model.petition.PetitionMainGroup;
import premium.gameserver.model.petition.PetitionSubGroup;
import premium.gameserver.utils.Language;

/**
 * @author VISTALL
 */
public class ExResponseShowStepTwo extends L2GameServerPacket
{
	private Language _language;
	private PetitionMainGroup _petitionMainGroup;
	
	public ExResponseShowStepTwo(Player player, PetitionMainGroup gr)
	{
		this._language = player.getLanguage();
		this._petitionMainGroup = gr;
	}
	
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xAF);
		Collection<PetitionSubGroup> subGroups = this._petitionMainGroup.getSubGroups();
		this.writeD(subGroups.size());
		this.writeS(this._petitionMainGroup.getDescription(this._language));
		for (PetitionSubGroup g : subGroups)
		{
			this.writeC(g.getId());
			this.writeS(g.getName(this._language));
		}
	}
}