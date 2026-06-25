package premium.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.dao.SiegePlayerDAO;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;
import premium.gameserver.model.entity.events.impl.SiegeEvent;
import premium.gameserver.model.entity.residence.Residence;
import premium.gameserver.model.pledge.Clan;

public class CTBSiegeClanObject extends SiegeClanObject
{
	private static final long serialVersionUID = 1L;
	private final List<Integer> _players = new ArrayList<>();
	private long _npcId;
	
	public CTBSiegeClanObject(String type, Clan clan, long param, long date)
	{
		super(type, clan, param, date);
		_npcId = param;
	}
	
	public CTBSiegeClanObject(String type, Clan clan, long param)
	{
		this(type, clan, param, System.currentTimeMillis());
	}
	
	public void select(Residence r)
	{
		_players.addAll(SiegePlayerDAO.select(r, getObjectId()));
	}
	
	public List<Integer> getPlayers()
	{
		return _players;
	}
	
	@Override
	public void setEvent(boolean start, @SuppressWarnings("rawtypes") SiegeEvent event)
	{
		for (int i : getPlayers())
		{
			final Player player = GameObjectsStorage.getPlayer(i);
			if (player != null)
			{
				if (start)
				{
					player.addEvent(event);
				}
				else
				{
					player.removeEvent(event);
				}
				player.broadcastCharInfo();
			}
		}
	}
	
	@Override
	public boolean isParticle(Player player)
	{
		return _players.contains(player.getObjectId());
	}
	
	@Override
	public long getParam()
	{
		return _npcId;
	}
	
	public void setParam(int npcId)
	{
		_npcId = npcId;
	}
}
