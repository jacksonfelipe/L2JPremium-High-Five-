package l2mv.gameserver.inertiax.model.panels;

import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.InertiaPanel;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class PathEditor extends InertiaPanel
{
	private final LinkedHashMap<String, InertiaPath> _inertiaPaths = new LinkedHashMap<>();
	private InertiaPath _selePath = null;

	public PathEditor(final Inertia inertia)
	{
		super(inertia);
	}

	public InertiaPath getSelePath()
	{
		return _selePath;
	}

	public LinkedHashMap<String, InertiaPath> getInertiaPaths()
	{
		return _inertiaPaths;
	}

	@Override
	public void render(final Player viewer)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1002);
		html.setFile("mods/autofarm/panels/patheditor/paths.htm");
		html.replace("%id%", String.valueOf(viewer.getObjectId()));

		StringBuilder sb = new StringBuilder();
		for (final InertiaPath path : _inertiaPaths.values())
		{
			String name = path.getName();
			sb.append("<tr>");
			sb.append("<td><font color=LEVEL>").append(name).append("</font> (").append(path.getPoints().size()).append(" pts)</td>");
			sb.append("<td><button value=\"Open\" action=\"bypass inertia_panel_path_editor ").append(viewer.getObjectId()).append(" openpath ").append(name).append("\" width=50 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			sb.append("<td><button value=\"Delete\" action=\"bypass inertia_panel_path_editor ").append(viewer.getObjectId()).append(" rempath ").append(name).append("\" width=50 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			sb.append("</tr>");
		}
		html.replace("%paths%", sb.toString());

		viewer.sendPacket(html);
	}

	@Override
	protected boolean onBypass(Player actor, String cmd, StringTokenizer st)
	{
		if (cmd.equals("renderpaths"))
		{
			render(actor);
			return true;
		}
		else if (cmd.equals("create"))
		{
			if (_inertiaPaths.size() > 9)
			{
				actor.sendMessage("You have too many paths!");
				return false;
			}
			
			if (st.hasMoreTokens())
			{
				final String pathName = st.nextToken().toUpperCase();
				
				if (pathName.length() > 20)
				{
					actor.sendMessage("This path name is too long (max 20)!");
					return false;
				}
				
				if (_inertiaPaths.containsKey(pathName))
				{
					actor.sendMessage("This path name already exists!");
					return false;
				}
				
				final InertiaPath inertiaPath = new InertiaPath(pathName, actor);
				_inertiaPaths.put(pathName, inertiaPath);
				
				inertiaPath.renderPath(actor);
				
				render(actor);
			}
			return true;
		}
		else if (cmd.equals("resetpaths"))
		{
			for (final InertiaPath path : _inertiaPaths.values())
				path.delete(actor);
			_inertiaPaths.clear();
			render(actor);
			return true;
		}
		else if (cmd.equals("hidepaths"))
		{
			actor.sendMessage("Hiding all paths.");
			for (final InertiaPath path : _inertiaPaths.values())
				path.hide(actor);
			render(actor);
			return true;
		}
		else if (cmd.equals("rempath"))
		{
			if (st.hasMoreTokens())
			{
				final String pathName = st.nextToken();
				final InertiaPath remPath = _inertiaPaths.remove(pathName);
				if (remPath != null)
				{
					remPath.delete(actor);
					render(actor);
				}
			}
			return true;
		}
		else if (cmd.equals("togpath"))
		{
			if (st.hasMoreTokens())
			{
				final String pathName = st.nextToken();
				final InertiaPath inertiaPath = _inertiaPaths.get(pathName);
				if (inertiaPath != null)
				{
					inertiaPath.toggleRender();
					inertiaPath.renderPath(actor);
				}
				render(actor);
			}
			return true;
		}
		else if (cmd.equals("openpath"))
		{
			if (st.hasMoreTokens())
			{
				final String pathName = st.nextToken();
				final InertiaPath inertiaPath = _inertiaPaths.get(pathName);
				if (inertiaPath != null)
				{
					inertiaPath.renderPage(actor);
					_selePath = inertiaPath;
				}
			}
			return true;
		}
		else if (cmd.equals("path"))
		{
			if (st.hasMoreTokens())
			{
				final String pathName = st.nextToken();
				final InertiaPath inertiaPath = _inertiaPaths.get(pathName);
				if (inertiaPath != null)
				{
					if (st.hasMoreTokens())
					{
						final String cmd2 = st.nextToken();
						return inertiaPath.onBypass(actor, cmd2, st);
					}
				}
			}
			return true;
		}
		else if (cmd.equals("sim"))
		{
			if (st.hasMoreTokens())
			{
				final String pathName = st.nextToken();
				final InertiaPath inertiaPath = _inertiaPaths.get(pathName);
				if (inertiaPath != null)
				{
					inertiaPath.sim(actor);
				}
			}
			return true;
		}

		return false;
	}
}
