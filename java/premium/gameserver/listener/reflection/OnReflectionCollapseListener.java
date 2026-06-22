package premium.gameserver.listener.reflection;

import premium.commons.listener.Listener;
import premium.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener extends Listener<Reflection>
{
	public void onReflectionCollapse(Reflection reflection);
}
