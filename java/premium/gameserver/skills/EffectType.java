package premium.gameserver.skills;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import premium.gameserver.model.Effect;
import premium.gameserver.skills.effects.EffectAddSkills;
import premium.gameserver.skills.effects.EffectAgathionRes;
import premium.gameserver.skills.effects.EffectAggression;
import premium.gameserver.skills.effects.EffectBetray;
import premium.gameserver.skills.effects.EffectBlessNoblesse;
import premium.gameserver.skills.effects.EffectBlockStat;
import premium.gameserver.skills.effects.EffectBluff;
import premium.gameserver.skills.effects.EffectBuff;
import premium.gameserver.skills.effects.EffectCPDamPercent;
import premium.gameserver.skills.effects.EffectCallSkills;
import premium.gameserver.skills.effects.EffectCharge;
import premium.gameserver.skills.effects.EffectCharmOfCourage;
import premium.gameserver.skills.effects.EffectCombatPointHealOverTime;
import premium.gameserver.skills.effects.EffectConsumeSoulsOverTime;
import premium.gameserver.skills.effects.EffectCubic;
import premium.gameserver.skills.effects.EffectCurseOfLifeFlow;
import premium.gameserver.skills.effects.EffectDamOverTime;
import premium.gameserver.skills.effects.EffectDamOverTimeLethal;
import premium.gameserver.skills.effects.EffectDebuffImmunity;
import premium.gameserver.skills.effects.EffectDestroySummon;
import premium.gameserver.skills.effects.EffectDisarm;
import premium.gameserver.skills.effects.EffectDiscord;
import premium.gameserver.skills.effects.EffectDispelEffects;
import premium.gameserver.skills.effects.EffectEnervation;
import premium.gameserver.skills.effects.EffectFakeDeath;
import premium.gameserver.skills.effects.EffectFear;
import premium.gameserver.skills.effects.EffectGrow;
import premium.gameserver.skills.effects.EffectHPDamPercent;
import premium.gameserver.skills.effects.EffectHate;
import premium.gameserver.skills.effects.EffectHeal;
import premium.gameserver.skills.effects.EffectHealBlock;
import premium.gameserver.skills.effects.EffectHealCPPercent;
import premium.gameserver.skills.effects.EffectHealOverTime;
import premium.gameserver.skills.effects.EffectHealPercent;
import premium.gameserver.skills.effects.EffectHourglass;
import premium.gameserver.skills.effects.EffectImmobilize;
import premium.gameserver.skills.effects.EffectInterrupt;
import premium.gameserver.skills.effects.EffectInvisible;
import premium.gameserver.skills.effects.EffectInvulnerable;
import premium.gameserver.skills.effects.EffectLDManaDamOverTime;
import premium.gameserver.skills.effects.EffectLockInventory;
import premium.gameserver.skills.effects.EffectMPDamPercent;
import premium.gameserver.skills.effects.EffectManaDamOverTime;
import premium.gameserver.skills.effects.EffectManaHeal;
import premium.gameserver.skills.effects.EffectManaHealOverTime;
import premium.gameserver.skills.effects.EffectManaHealPercent;
import premium.gameserver.skills.effects.EffectMeditation;
import premium.gameserver.skills.effects.EffectMute;
import premium.gameserver.skills.effects.EffectMuteAll;
import premium.gameserver.skills.effects.EffectMuteAttack;
import premium.gameserver.skills.effects.EffectMutePhisycal;
import premium.gameserver.skills.effects.EffectNegateEffects;
import premium.gameserver.skills.effects.EffectNegateMusic;
import premium.gameserver.skills.effects.EffectParalyze;
import premium.gameserver.skills.effects.EffectPetrification;
import premium.gameserver.skills.effects.EffectRandomHate;
import premium.gameserver.skills.effects.EffectRelax;
import premium.gameserver.skills.effects.EffectRemoveTarget;
import premium.gameserver.skills.effects.EffectRestoration;
import premium.gameserver.skills.effects.EffectRestorationRandom;
import premium.gameserver.skills.effects.EffectRoot;
import premium.gameserver.skills.effects.EffectSalvation;
import premium.gameserver.skills.effects.EffectServitorShare;
import premium.gameserver.skills.effects.EffectSilentMove;
import premium.gameserver.skills.effects.EffectSleep;
import premium.gameserver.skills.effects.EffectStun;
import premium.gameserver.skills.effects.EffectSummonHealPercent;
import premium.gameserver.skills.effects.EffectSummonManaHealPercent;
import premium.gameserver.skills.effects.EffectSymbol;
import premium.gameserver.skills.effects.EffectTemplate;
import premium.gameserver.skills.effects.EffectTransformation;
import premium.gameserver.skills.effects.EffectUnAggro;
import premium.gameserver.skills.effects.EffectVitalityDamOverTime;
import premium.gameserver.skills.effects.EffectVitalityStop;
import premium.gameserver.stats.Env;
import premium.gameserver.stats.Stats;

public enum EffectType
{
	// The main effects
	AddSkills(EffectAddSkills.class, null, false),
	AgathionResurrect(EffectAgathionRes.class, null, true),
	Aggression(EffectAggression.class, null, true),
	Betray(EffectBetray.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	BlessNoblesse(EffectBlessNoblesse.class, null, true),
	BlockStat(EffectBlockStat.class, null, true),
	Buff(EffectBuff.class, null, false),
	Bluff(EffectBluff.class, AbnormalEffect.NULL, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	DebuffImmunity(EffectDebuffImmunity.class, null, true),
	DispelEffects(EffectDispelEffects.class, null, Stats.CANCEL_RESIST, Stats.CANCEL_POWER, true),
	CallSkills(EffectCallSkills.class, null, false),
	CombatPointHealOverTime(EffectCombatPointHealOverTime.class, null, true),
	ConsumeSoulsOverTime(EffectConsumeSoulsOverTime.class, null, true),
	Charge(EffectCharge.class, null, false),
	CharmOfCourage(EffectCharmOfCourage.class, null, true),
	CPDamPercent(EffectCPDamPercent.class, null, true),
	Cubic(EffectCubic.class, null, true),
	DamOverTime(EffectDamOverTime.class, null, false),
	DamOverTimeLethal(EffectDamOverTimeLethal.class, null, false),
	DestroySummon(EffectDestroySummon.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	Disarm(EffectDisarm.class, null, true),
	Discord(EffectDiscord.class, AbnormalEffect.CONFUSED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	Enervation(EffectEnervation.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, false),
	FakeDeath(EffectFakeDeath.class, null, true),
	Fear(EffectFear.class, AbnormalEffect.AFFRAID, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	Grow(EffectGrow.class, AbnormalEffect.GROW, false),
	Hate(EffectHate.class, null, false),
	Heal(EffectHeal.class, null, false),
	HealBlock(EffectHealBlock.class, null, true),
	HealCPPercent(EffectHealCPPercent.class, null, true),
	HealOverTime(EffectHealOverTime.class, null, false),
	HealPercent(EffectHealPercent.class, null, false),
	SummonHealPercent(EffectSummonHealPercent.class, null, false),
	HPDamPercent(EffectHPDamPercent.class, null, true),
	IgnoreSkill(EffectBuff.class, null, false),
	Immobilize(EffectImmobilize.class, null, true),
	Interrupt(EffectInterrupt.class, null, true),
	Invulnerable(EffectInvulnerable.class, null, false),
	Invisible(EffectInvisible.class, null, false),
	LockInventory(EffectLockInventory.class, null, false),
	CurseOfLifeFlow(EffectCurseOfLifeFlow.class, null, true),
	LDManaDamOverTime(EffectLDManaDamOverTime.class, null, true),
	ManaDamOverTime(EffectManaDamOverTime.class, null, true),
	ManaHeal(EffectManaHeal.class, null, false),
	ManaHealOverTime(EffectManaHealOverTime.class, null, false),
	ManaHealPercent(EffectManaHealPercent.class, null, false),
	SummonManaHealPercent(EffectSummonManaHealPercent.class, null, false),
	Meditation(EffectMeditation.class, null, false),
	MPDamPercent(EffectMPDamPercent.class, null, true),
	Mute(EffectMute.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	MuteAll(EffectMuteAll.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	MuteAttack(EffectMuteAttack.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	MutePhisycal(EffectMutePhisycal.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
	NegateEffects(EffectNegateEffects.class, null, false),
	NegateMusic(EffectNegateMusic.class, null, false),
	Paralyze(EffectParalyze.class, AbnormalEffect.HOLD_1, Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER, true),
	Petrification(EffectPetrification.class, AbnormalEffect.HOLD_2, Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER, true),
	RandomHate(EffectRandomHate.class, null, true),
	Relax(EffectRelax.class, null, true),
	RemoveTarget(EffectRemoveTarget.class, null, true),
	Restoration(EffectRestoration.class, null, true),
	RestorationRandom(EffectRestorationRandom.class, null, true),
	Root(EffectRoot.class, AbnormalEffect.ROOT, Stats.ROOT_RESIST, Stats.ROOT_POWER, true),
	Hourglass(EffectHourglass.class, null, true),
	Salvation(EffectSalvation.class, null, true),
	ServitorShare(EffectServitorShare.class, null, false),
	SilentMove(EffectSilentMove.class, AbnormalEffect.STEALTH, true),
	Sleep(EffectSleep.class, AbnormalEffect.SLEEP, Stats.SLEEP_RESIST, Stats.SLEEP_POWER, true),
	Stun(EffectStun.class, AbnormalEffect.STUN, Stats.STUN_RESIST, Stats.STUN_POWER, true),
	Symbol(EffectSymbol.class, null, false),
	Transformation(EffectTransformation.class, null, true),
	UnAggro(EffectUnAggro.class, null, true),
	Vitality(EffectBuff.class, AbnormalEffect.VITALITY, true),
	VitalityMaintenance(EffectBuff.class, AbnormalEffect.VITALITY, true),
	VitalityStop(EffectVitalityStop.class, null, true),
	VitalityDamOverTime(EffectVitalityDamOverTime.class, AbnormalEffect.VITALITY, true),
	// Производные от основных эффектов
	Poison(EffectDamOverTime.class, null, Stats.POISON_RESIST, Stats.POISON_POWER, false),
	PoisonLethal(EffectDamOverTimeLethal.class, null, Stats.POISON_RESIST, Stats.POISON_POWER, false),
	Bleed(EffectDamOverTime.class, null, Stats.BLEED_RESIST, Stats.BLEED_POWER, false),
	Debuff(EffectBuff.class, null, false),
	WatcherGaze(EffectBuff.class, null, false),
	
	AbsorbDamageToEffector(EffectBuff.class, null, false), // абсорбирует часть дамага к еффектора еффекта
	AbsorbDamageToMp(EffectBuff.class, AbnormalEffect.S_ARCANE_SHIELD, false), // абсорбирует часть дамага в мп
	AbsorbDamageToSummon(EffectLDManaDamOverTime.class, null, true); // абсорбирует часть дамага к сумону
	
	private final Constructor<? extends Effect> _constructor;
	private final AbnormalEffect _abnormal;
	private final Stats _resistType;
	private final Stats _attributeType;
	private final boolean _isRaidImmune;
	
	private EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, boolean isRaidImmune)
	{
		this(clazz, abnormal, null, null, isRaidImmune);
	}
	
	private EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, Stats resistType, Stats attributeType, boolean isRaidImmune)
	{
		try
		{
			_constructor = clazz.getConstructor(Env.class, EffectTemplate.class);
		}
		catch (NoSuchMethodException e)
		{
			throw new Error(e);
		}
		_abnormal = abnormal;
		_resistType = resistType;
		_attributeType = attributeType;
		_isRaidImmune = isRaidImmune;
	}
	
	public AbnormalEffect getAbnormal()
	{
		return _abnormal;
	}
	
	public Stats getResistType()
	{
		return _resistType;
	}
	
	public Stats getAttributeType()
	{
		return _attributeType;
	}
	
	public boolean isRaidImmune()
	{
		return _isRaidImmune;
	}
	
	public Effect makeEffect(Env env, EffectTemplate template) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		return _constructor.newInstance(env, template);
	}
}