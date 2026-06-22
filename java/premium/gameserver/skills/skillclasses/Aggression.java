package premium.gameserver.skills.skillclasses;

import java.util.List;

import premium.gameserver.ai.CtrlEvent;
import premium.gameserver.model.Creature;
import premium.gameserver.model.Skill;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.templates.StatsSet;

public class Aggression extends Skill
{
	private final boolean _unaggring;
	private final boolean _silent;
	private final boolean ignorePlayables;
	private final boolean autoAttack;
	
	public Aggression(StatsSet set)
	{
		super(set);
		_unaggring = set.getBool("unaggroing", false);
		_silent = set.getBool("silent", false);
		ignorePlayables = set.getBool("ignorePlayables", false);
		autoAttack = set.getBool("autoAttack", false);
	}
	
	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		int effect = _effectPoint;
		
		if (isSSPossible() && (activeChar.getChargedSoulShot() || activeChar.getChargedSpiritShot() > 0))
		{
			effect *= 2;
		}
		
		for (Creature target : targets)
		{
			if (target != null)
			{
				if (!target.isAutoAttackable(activeChar))
				{
					continue;
				}
				if (target.isNpc())
				{
					if (_unaggring)
					{
						if (target.isNpc() && activeChar.isPlayable())
						{
							((NpcInstance) target).getAggroList().addDamageHate(activeChar, 0, -effect);
						}
					}
					else
					{
						target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, effect);
						if (!_silent)
						{
							target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar, 0);
						}
					}
				}
				else if (!ignorePlayables && target.isPlayable() && !target.isDebuffImmune())
				{
					target.setTarget(activeChar);
					if (autoAttack)
					{
						target.getAI().Attack(activeChar, false, false);
					}
				}
				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
		}
		
		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}