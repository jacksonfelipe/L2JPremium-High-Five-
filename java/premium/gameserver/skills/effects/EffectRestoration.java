package premium.gameserver.skills.effects;

import premium.gameserver.model.Effect;
import premium.gameserver.model.Playable;
import premium.gameserver.stats.Env;
import premium.gameserver.utils.ItemFunctions;

public class EffectRestoration extends Effect
{
	private final int itemId;
	private final long count;
	
	public EffectRestoration(Env env, EffectTemplate template)
	{
		super(env, template);
		final String item = getTemplate().getParam().getString("Item");
		itemId = Integer.parseInt(item.split(":")[0]);
		count = Long.parseLong(item.split(":")[1]);
	}
	
	public EffectRestoration(Effect effect)
	{
		super(effect);
		final String item = getTemplate().getParam().getString("Item");
		itemId = Integer.parseInt(item.split(":")[0]);
		count = Long.parseLong(item.split(":")[1]);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		ItemFunctions.addItem((Playable) getEffected(), itemId, count, true, "EffectRestoration");
	}
	
	@Override
	protected boolean onActionTime()
	{
		return false;
	}
}
