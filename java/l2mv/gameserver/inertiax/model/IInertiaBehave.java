package l2mv.gameserver.inertiax.model;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;

public interface IInertiaBehave
{
	public default void expand(IInertiaBehave behave)
	{
		
	}
	
	void onThinkStart();
	
	void onThinkEnd();
	
	void onDeath(Creature killer);
	
	void onKill(Creature victim);
	
	void onAttack(Creature target);
	
	void onSkillCast(Skill skill);
	
	void onNewTarget(Creature oldTarget, Creature newTarget);
	
	boolean filterSkill(Skill skill);
	
	boolean filterTarget(Creature target);
	
	void whileDead();
	
	void whileTargetDead();
	
	void onCreditsEnd();
	
	float lagMultiplier();
	
	void onUntarget();

	void onFollowClose(Player assistPlayer);

	void onFollowFar(Player assistPlayer);

	void onAssistNoTarget(Player assistPlayer);

	void onStartAutoAttack(Creature actualTarget);
	
	public void setInertia(final Inertia inertia);
	
	public Inertia getInertia();
	
	public default int getPriority()
	{
		return 0;
	}
	
	public Creature searchTarget();
	
	public void onNoTargetFound();
}
