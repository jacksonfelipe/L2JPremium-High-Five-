package l2mv.gameserver.inertiax.model.filters;

import java.util.function.Predicate;

import l2mv.gameserver.inertiax.model.Inertia;

public abstract class InertiaFilter<E> implements Predicate<E>
{
	protected final Inertia _inertia;
	
	public InertiaFilter(final Inertia inertia)
	{
		_inertia = inertia;
	}

}
