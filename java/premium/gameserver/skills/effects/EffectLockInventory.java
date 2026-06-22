package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.LockType;
import premium.gameserver.stats.Env;

public class EffectLockInventory extends Effect
{
	private LockType _lockType;
	private int[] _lockItems;
	
	public EffectLockInventory(Env env, EffectTemplate template)
	{
		super(env, template);
		_lockType = template.getParam().getEnum("lockType", LockType.class);
		_lockItems = template.getParam().getIntegerArray("lockItems");
	}
	
	public EffectLockInventory(Effect effect)
	{
		super(effect);
		_lockType = getTemplate().getParam().getEnum("lockType", LockType.class);
		_lockItems = getTemplate().getParam().getIntegerArray("lockItems");
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		Player player = _effector.getPlayer();
		
		player.getInventory().lockItems(_lockType, _lockItems);
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		
		Player player = _effector.getPlayer();
		
		player.getInventory().unlock();
	}
	
	@Override
	protected boolean onActionTime()
	{
		return false;
	}
}
