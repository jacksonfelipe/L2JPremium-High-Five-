package ai;

import premium.commons.util.Rnd;
import premium.gameserver.ai.CtrlIntention;
import premium.gameserver.ai.Fighter;
import premium.gameserver.geodata.GeoEngine;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.components.ChatType;
import premium.gameserver.network.serverpackets.components.NpcString;
import premium.gameserver.scripts.Functions;
import premium.gameserver.utils.Location;

/**
 * Contaminated Mucrokian (22654). Кричит в чат перед атакой. Игнорирует атаку стражей и убегает.
 */
public class ContaminatedMucrokian extends Fighter
{
	
	public ContaminatedMucrokian(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}
		if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
		{
			Functions.npcSay(actor, NpcString.NAIA_WAGANAGEL_PEUTAGUN, ChatType.NPC_ALL, 5000);
		}
		super.onIntentionAttack(target);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor != null && !actor.isDead())
		{
			if (attacker != null)
			{
				if (attacker.getNpcId() >= 22656 && attacker.getNpcId() <= 22659)
				{
					if (Rnd.chance(100))
					{
						actor.abortAttack(true, false);
						actor.getAggroList().clear();
						Location pos = Location.findPointToStay(actor, 450, 600);
						if (GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
						{
							actor.setRunning();
							addTaskMove(pos, false);
						}
					}
				}
			}
		}
		super.onEvtAttacked(attacker, damage);
	}
}