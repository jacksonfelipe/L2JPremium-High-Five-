package ai.Zone.DragonValley.DV_RB;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.tables.SkillTable;

/**
 * @author FandC
 */

public class SpikeSlasherMinion extends Fighter
{
	
	private Skill paralysis = SkillTable.getInstance().getInfo(6878, 1);
	
	private long last_cast_anchor = 0;
	
	public SpikeSlasherMinion(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (last_cast_anchor < System.currentTimeMillis())
		{
			actor.doCast(paralysis, attacker, true);
			last_cast_anchor = System.currentTimeMillis() + Rnd.get(5, 10) * 1000;
		}
		super.onEvtAttacked(attacker, damage);
	}
	
}
