package premium.gameserver.inertiax.model;

import java.util.StringTokenizer;

import premium.gameserver.inertiax.model.ext.InertiaExt;
import premium.gameserver.model.Player;

public abstract class InertiaPanel extends InertiaExt
{
	protected final int _ownerId;
	
	protected final Inertia _inertia;
	
	public InertiaPanel(final Inertia inertia)
	{
		_inertia = inertia;
		_ownerId = inertia.getOwnerId();
		
		inertia.addInertiaExt(this);
	}

	public abstract void render(final Player viewer);
	
	public final boolean onBypass(final Player actor, final StringTokenizer st)
	{
		if (st.hasMoreTokens())
		{
			final String cmd = st.nextToken();
			
			if (cmd.startsWith("render"))
				render(actor);
			else
				return onBypass(actor, cmd, st);
		}
		
		return false;
	}
	
	protected boolean onBypass(final Player actor, final String cmd, final StringTokenizer st)
	{
		return false;
	}
	
	public void onItemDrop(final int itemId, final long count) {}
	
	public void renderInertia(final Player viewer)
	{
		_inertia.render(viewer);
	}
}
