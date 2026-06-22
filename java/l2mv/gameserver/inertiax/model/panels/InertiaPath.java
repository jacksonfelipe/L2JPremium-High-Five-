package l2mv.gameserver.inertiax.model.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.network.serverpackets.ExServerPrimitive;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class InertiaPath
{
	private final String _name;
	private final List<Location> _points = new ArrayList<>();
	private boolean _render = true;
	private boolean _designMode = false;
	private int _currentWaypointIndex = 0;
	private boolean _forward = true;

	public int getCurrentWaypointIndex()
	{
		return _currentWaypointIndex;
	}

	public void setCurrentWaypointIndex(int index)
	{
		this._currentWaypointIndex = index;
		this._forward = true;
	}

	public void nextWaypoint()
	{
		if (_points.isEmpty())
			return;
		if (_points.size() == 1)
		{
			_currentWaypointIndex = 0;
			return;
		}
		
		if (_forward)
		{
			if (_currentWaypointIndex >= _points.size() - 1)
			{
				_forward = false;
				_currentWaypointIndex--;
			}
			else
			{
				_currentWaypointIndex++;
			}
		}
		else
		{
			if (_currentWaypointIndex <= 0)
			{
				_forward = true;
				_currentWaypointIndex++;
			}
			else
			{
				_currentWaypointIndex--;
			}
		}
	}

	public InertiaPath(String name, Player player)
	{
		this._name = name;
	}

	public String getName()
	{
		return _name;
	}

	public List<Location> getPoints()
	{
		return _points;
	}

	public boolean isDesignMode()
	{
		return _designMode;
	}

	public void setDesignMode(boolean val)
	{
		this._designMode = val;
	}

	public void toggleRender()
	{
		this._render = !this._render;
	}

	public boolean isRender()
	{
		return _render;
	}

	public void addPoint(Location loc)
	{
		if (_points.size() < 10)
		{
			_points.add(loc);
		}
	}

	public void removePoint(int index)
	{
		if (index >= 0 && index < _points.size())
		{
			_points.remove(index);
		}
	}

	public void renderPath(Player player)
	{
		if (!_render || _points.isEmpty())
		{
			hide(player);
			return;
		}

		ExServerPrimitive prim = new ExServerPrimitive("Path_" + _name, player.getX(), player.getY(), player.getZ());
		Color color = Color.GREEN;

		for (int i = 0; i < _points.size(); i++)
		{
			Location loc = _points.get(i);

			if (i > 0)
			{
				Location prev = _points.get(i - 1);
				prim.addLine(color, prev.x, prev.y, prev.z, loc.x, loc.y, loc.z);

				// Draw direction arrow/triangle in the middle of the segment
				double dx = loc.x - prev.x;
				double dy = loc.y - prev.y;
				double len = Math.sqrt(dx * dx + dy * dy);
				if (len > 40)
				{
					double ux = dx / len;
					double uy = dy / len;
					double px = -uy;
					double py = ux;
					
					// Triangle at the midpoint
					double midX = (prev.x + loc.x) / 2.0;
					double midY = (prev.y + loc.y) / 2.0;
					double midZ = (prev.z + loc.z) / 2.0;
					
					int tipX = (int) (midX + ux * 20);
					int tipY = (int) (midY + uy * 20);
					
					int baseLeftX = (int) (midX - ux * 10 + px * 12);
					int baseLeftY = (int) (midY - uy * 10 + py * 12);
					
					int baseRightX = (int) (midX - ux * 10 - px * 12);
					int baseRightY = (int) (midY - uy * 10 - py * 12);
					
					Color arrowColor = Color.RED;
					prim.addLine(arrowColor, baseLeftX, baseLeftY, (int) midZ, tipX, tipY, (int) midZ);
					prim.addLine(arrowColor, baseRightX, baseRightY, (int) midZ, tipX, tipY, (int) midZ);
					prim.addLine(arrowColor, baseLeftX, baseLeftY, (int) midZ, baseRightX, baseRightY, (int) midZ);
				}
			}
		}

		player.sendPacket(prim);
	}

	public void hide(Player player)
	{
		ExServerPrimitive prim = new ExServerPrimitive("Path_" + _name, player.getX(), player.getY(), player.getZ());
		player.sendPacket(prim);
	}

	public void delete(Player player)
	{
		hide(player);
		_points.clear();
	}

	public void renderPage(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1002);
		html.setFile("mods/autofarm/panels/patheditor/path.htm");
		html.replace("%id%", String.valueOf(player.getObjectId()));
		html.replace("%path%", _name);

		String tog;
		if (_designMode)
		{
			tog = "<td align=right><button value=\"Add Point\" action=\"bypass inertia_panel_path_editor " + player.getObjectId() + " path " + _name + " add\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" +
			      "<td align=right><button value=\"Stop\" action=\"bypass inertia_panel_path_editor " + player.getObjectId() + " path " + _name + " stop\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
		}
		else
		{
			tog = "<td align=right><button value=\"Start\" action=\"bypass inertia_panel_path_editor " + player.getObjectId() + " path " + _name + " start\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
		}
		html.replace("%tog%", tog);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < _points.size(); i++)
		{
			Location loc = _points.get(i);
			String name = String.valueOf((char)('A' + i));
			sb.append("<tr>");
			sb.append("<td><font color=LEVEL>").append(name).append("</font> - X:").append(loc.x).append(" Y:").append(loc.y).append("</td>");
			sb.append("<td><button value=\"Remove\" action=\"bypass inertia_panel_path_editor ").append(player.getObjectId()).append(" path ").append(_name).append(" remove ").append(i).append("\" width=60 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			sb.append("<td><button value=\"Select\" action=\"bypass inertia_panel_path_editor ").append(player.getObjectId()).append(" path ").append(_name).append(" select ").append(i).append("\" width=60 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			sb.append("</tr>");
		}
		html.replace("%paths%", sb.toString());
		html.replace("%hid%", _render ? "Hide" : "Show");

		player.sendPacket(html);
	}

	public boolean onBypass(Player actor, String cmd, StringTokenizer st)
	{
		if (cmd.equals("start"))
		{
			_designMode = true;
			renderPage(actor);
			return true;
		}
		else if (cmd.equals("stop"))
		{
			_designMode = false;
			renderPage(actor);
			return true;
		}
		else if (cmd.equals("add"))
		{
			if (_points.size() >= 10)
			{
				actor.sendMessage("Path is full (max 10 points)!");
			}
			else
			{
				addPoint(actor.getLoc());
				renderPath(actor);
				actor.sendMessage("Added waypoint " + (char)('A' + _points.size() - 1));
			}
			renderPage(actor);
			return true;
		}
		else if (cmd.equals("remove"))
		{
			if (st.hasMoreTokens())
			{
				int idx = Integer.parseInt(st.nextToken());
				removePoint(idx);
				renderPath(actor);
				renderPage(actor);
			}
			return true;
		}
		else if (cmd.equals("select"))
		{
			if (st.hasMoreTokens())
			{
				int idx = Integer.parseInt(st.nextToken());
				if (idx >= 0 && idx < _points.size())
				{
					Location loc = _points.get(idx);
					actor.moveToLocation(loc, 0, true);
					actor.sendMessage("Moving to waypoint " + (char)('A' + idx));
				}
			}
			return true;
		}
		else if (cmd.equals("hide"))
		{
			toggleRender();
			renderPath(actor);
			renderPage(actor);
			return true;
		}
		return false;
	}

	public void sim(Player player)
	{
		if (_points.isEmpty())
		{
			player.sendMessage("Path is empty!");
			return;
		}
		
		Location first = _points.get(0);
		player.moveToLocation(first, 0, true);
		player.sendMessage("Simulating path starting at waypoint A.");
	}
}
