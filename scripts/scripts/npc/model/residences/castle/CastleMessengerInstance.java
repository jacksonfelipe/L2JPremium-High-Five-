package npc.model.residences.castle;

import premium.gameserver.model.Player;
import premium.gameserver.model.entity.residence.Castle;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.CastleSiegeInfo;
import premium.gameserver.templates.npc.NpcTemplate;

public class CastleMessengerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public CastleMessengerInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		Castle castle = getCastle();
		
		if (player.isCastleLord(castle.getId()))
		{
			if (castle.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, "residence2/castle/sir_tyron021.htm");
			}
			else
			{
				showChatWindow(player, "residence2/castle/sir_tyron007.htm");
			}
		}
		else if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress())
		{
			showChatWindow(player, "residence2/castle/sir_tyron021.htm");
		}
		else
		{
			player.sendPacket(new CastleSiegeInfo(castle, player));
		}
	}
}