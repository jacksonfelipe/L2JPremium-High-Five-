package events.Christmas;

import java.io.File;

import premium.commons.util.Rnd;
import premium.gameserver.ai.DefaultAI;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.World;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.skills.SkillsEngine;
import premium.gameserver.skills.effects.EffectTemplate;
import premium.gameserver.stats.Stats;
import premium.gameserver.stats.funcs.FuncTemplate;

public class ctreeAI extends DefaultAI
{
	private static final File ORIGINAL_EFFECT_FILE = new File("data/stats/skills/2100-2199.xml");
	private static final int ORIGINAL_EFFECT_ID = 2139;
	private static final int RANGE = 200;
	private final Skill treeEffect;
	
	public ctreeAI(NpcInstance actor)
	{
		super(actor);
		treeEffect = getRandomTreeEffect();
	}
	
	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}
		
		for (Player player : World.getAroundPlayers(actor, RANGE, RANGE))
		{
			if (player != null && player.getEffectList().getEffectsBySkillId(ORIGINAL_EFFECT_ID) == null)
			{
				actor.doCast(treeEffect, player, true);
			}
		}
		return false;
	}
	
	private static Skill getRandomTreeEffect()
	{
		int random = Rnd.get(7);
		switch (random)
		{
			case 0:
				return createRandomSkillEffect(Stats.POWER_DEFENCE, 1.25);
			case 1:
				return createRandomSkillEffect(Stats.POWER_ATTACK, 1.25);
			case 2:
				return createRandomSkillEffect(Stats.MAGIC_ATTACK, 1.25);
			case 3:
				return createRandomSkillEffect(Stats.CRITICAL_DAMAGE, 1.15);
			case 4:
				return createRandomSkillEffect(Stats.MAGIC_ATTACK_SPEED, 1.15);
			case 5:
				return createRandomSkillEffect(Stats.POWER_ATTACK_SPEED, 1.15);
			case 6:
				return createRandomSkillEffect(Stats.CRITICAL_RATE, 1.15);
			default:
				return createRandomSkillEffect(Stats.CRITICAL_RATE, 1.15);
		}
	}
	
	private static Skill createRandomSkillEffect(Stats stat, double mult)
	{
		Skill copiedSkill = SkillsEngine.getInstance().loadSkill(ORIGINAL_EFFECT_ID, ORIGINAL_EFFECT_FILE);
		changeSkillEffect(copiedSkill, stat, mult);
		return copiedSkill;
	}
	
	private static void changeSkillEffect(Skill skill, Stats stat, double mult)
	{
		FuncTemplate func = new FuncTemplate(null, "Mul", stat, 0x30, mult);
		for (EffectTemplate template : skill.getEffectTemplates())
		{
			template.clearAttachedFuncs();
			template.attachFunc(func);
		}
	}
	
	@Override
	protected boolean randomAnimation()
	{
		return false;
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}