package ai.PaganTemplete;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Mystic;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Player;
import premium.gameserver.model.World;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.utils.Location;

/**
 * @author claww - AI for Rabe Boss Andreas Van Halter (29062). - All the information about the AI ​​painted. - AI is tested and works.
 */
public class TriolsBeliever extends Mystic
{
	private boolean _tele = true;
	
	public static final Location[] locs =
	{
		new Location(-16128, -35888, -10726),
		new Location(-16397, -44970, -10724),
		new Location(-15729, -42001, -10724)
	};
	
	public TriolsBeliever(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}
		
		for (Player player : World.getAroundPlayers(actor, 500, 500))
		{
			if (player == null || !player.isInParty())
			{
				continue;
			}
			
			if (player.getParty().size() >= 5 && _tele)
			{
				_tele = false;
				player.teleToLocation(Rnd.get(locs));
			}
		}
		
		return true;
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		_tele = true;
		super.onEvtDead(killer);
	}
}