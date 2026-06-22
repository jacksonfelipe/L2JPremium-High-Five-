package ai;

import premium.commons.util.Rnd;
import premium.gameserver.ai.Fighter;
import premium.gameserver.model.Creature;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.components.NpcString;
import premium.gameserver.scripts.Functions;

public class FrightenedOrc extends Fighter
{
	private boolean _sayOnAttack;
	
	public FrightenedOrc(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtSpawn()
	{
		_sayOnAttack = true;
		super.onEvtSpawn();
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker != null && Rnd.chance(10) && _sayOnAttack)
		{
			Functions.npcSay(actor, NpcString.DONT_KILL_ME_PLEASE);
			_sayOnAttack = false;
		}
		
		super.onEvtAttacked(attacker, damage);
	}
	
}