package l2mv.gameserver.inertiax.model;

import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.tables.SkillTable;

public class InertiaCast
{
	private final int		_actionId;
	private final boolean	_isSkill;
	private double			_userHp	= 100;
	private double			_targHp	= 100;
	private long			_reuse;
	private long			_lastUse;
	
	public InertiaCast(final int actionId, final boolean isSkill)
	{
		_actionId = actionId;
		_isSkill = isSkill;
	}
	
	public int getActionId()
	{
		return _actionId;
	}
	
	public String getIcon()
	{
		if (_isSkill)
		{
			final Skill skill = SkillTable.getInstance().getInfo(_actionId, 1);
			return skill != null ? skill.getIcon() : "icon.etc_question_mark_i00";
		}
		else
		{
			final var template = ItemHolder.getInstance().getTemplate(_actionId);
			return template != null ? template.getIcon() : "icon.etc_question_mark_i00";
		}
	}
	
	public boolean isReuse()
	{
		return _lastUse + _reuse > System.currentTimeMillis();
	}
	
	public void initReuse()
	{
		_lastUse = System.currentTimeMillis();
	}
	
	public double getReuse()
	{
		return _reuse / 1000d;
	}
	
	public void setReuse(final double reuseSec)
	{
		_reuse = Math.min((long) (reuseSec * 1000L), 300_000);
	}
	
	public void setUserHP(final double userHp)
	{
		_userHp = Math.min(100d, userHp);
	}
	
	public boolean isUserHp(final Player player)
	{
		return player.getCurrentHpPercents() <= _userHp;
	}
	
	public double getUserHp()
	{
		return _userHp;
	}
	
	public void setTargetHP(final double targHp)
	{
		_targHp = Math.min(100d, targHp);
	}
	
	public boolean isTargetHp(final Creature target)
	{
		return target.getCurrentHpPercents() <= _targHp;
	}
	
	public double getTargetHp()
	{
		return _targHp;
	}
	
	public boolean isSkill()
	{
		return _isSkill;
	}
	
	public boolean isUsableNow(final Player player)
	{
		if (isReuse())
			return false;
		
		if (isSkill())
		{
			final int level = player.getSkillLevel(_actionId);
			if (level > 0)
			{
				final Skill skill = SkillTable.getInstance().getInfo(_actionId, level);
				if (skill == null || player.isSkillDisabled(skill))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
