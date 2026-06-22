package l2mv.gameserver.inertiax.enums;

import java.util.function.Function;

import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.InertiaPanel;
import l2mv.gameserver.inertiax.model.panels.DropTracker;
import l2mv.gameserver.inertiax.model.panels.PathEditor;
import l2mv.gameserver.inertiax.model.panels.RangeEditor;
import l2mv.gameserver.inertiax.model.panels.TargetFiltering;
import l2mv.gameserver.model.Player;

public enum EPanelOption
{
	Target_Filter(TargetFiltering::new),
	Drops_Tracker(DropTracker::new),
	Range_Editor(RangeEditor::new),
//	Activity_Stats(TargetFiltering::new),
//	InertiaX_Info(TargetFiltering::new),
	Path_Editor(PathEditor::new),
	;
	
	private final Function<Inertia, InertiaPanel> _panelSupplier;
	
	private EPanelOption(final Function<Inertia, InertiaPanel> panelSupplier)
	{
		_panelSupplier = panelSupplier;
	}
	
	@Override
	public String toString()
	{
		return (ordinal() + 1) + " " + super.toString().replace('_', ' ');
	}

	public void render(final Inertia inertia, final Player player)
	{
		final InertiaPanel inertiaPanel = inertia.fetchPanel(this);
		inertiaPanel.render(player);
	}
	
	public Function<Inertia, InertiaPanel> getCompute()
	{
		return _panelSupplier;
	}
	
	public String toLowerCase()
	{
		return super.toString().toLowerCase();
	}
}
