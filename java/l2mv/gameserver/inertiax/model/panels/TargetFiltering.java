package l2mv.gameserver.inertiax.model.panels;

import java.util.StringTokenizer;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import gnu.trove.set.hash.TIntHashSet;
import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.InertiaPanel;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class TargetFiltering extends InertiaPanel
{
	private final TIntHashSet _filteredIds = new TIntHashSet();
	
	public TargetFiltering(Inertia inertia)
	{
		super(inertia);
	}

	@Override
	public void render(final Player viewer)
	{
		final var player = _inertia.getActivePlayer();
		if (player == null)
			return;
		
		final var npcHtml = new NpcHtmlMessage(1003);
		npcHtml.setFile("mods/autofarm/panels/patheditor/targetfilter.htm");

		npcHtml.replace("%id%", String.valueOf(_ownerId));

		final Set<Integer> surroundingNpcIds = new HashSet<>();
		final List<NpcInstance> npcs = World.getAroundNpc(player, 2000, 300);
		if (npcs != null)
		{
			for (final NpcInstance npc : npcs)
			{
				if (npc != null)
				{
					surroundingNpcIds.add(npc.getNpcId());
				}
			}
		}

		for (final int id : _filteredIds.toArray())
		{
			surroundingNpcIds.add(id);
		}

		final StringBuilder sb = new StringBuilder();
		for (final int npcId : surroundingNpcIds)
		{
			final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
			if (template == null)
				continue;

			final String name = template.name;
			final boolean isFiltered = isFilteredId(npcId);
			
			sb.append("<tr>");
			sb.append("<td><font color=LEVEL>").append(name).append("</font></td>");
			if (isFiltered)
			{
				sb.append("<td><font color=FF0000>Filtered</font></td>");
				sb.append("<td><button value=\"Farm\" action=\"bypass inertia_panel_target_filter ").append(_ownerId).append(" toggle ").append(npcId).append("\" width=55 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			}
			else
			{
				sb.append("<td><font color=00FF00>Farming</font></td>");
				sb.append("<td><button value=\"Filter\" action=\"bypass inertia_panel_target_filter ").append(_ownerId).append(" toggle ").append(npcId).append("\" width=55 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			}
			sb.append("</tr>");
		}

		if (surroundingNpcIds.isEmpty())
		{
			sb.append("<tr><td align=center colspan=3><font color=LEVEL>No monsters found nearby.</font></td></tr>");
		}

		npcHtml.replace("%filt%", sb.toString());
		npcHtml.replace("%r%", String.valueOf(_filteredIds.size()));
		
		player.sendPacket(npcHtml);
	}

	@Override
	protected boolean onBypass(final Player actor, final String cmd, final StringTokenizer st)
	{
		if (cmd.equals("render"))
		{
			render(actor);
			return true;
		}
		else if (cmd.equals("reset"))
		{
			_filteredIds.clear();
			actor.sendMessage("Target filters reset.");
			render(actor);
			return true;
		}
		else if (cmd.equals("toggle"))
		{
			if (st.hasMoreTokens())
			{
				final int npcId = Integer.parseInt(st.nextToken());
				final boolean filtered = toggleFilteredId(npcId);
				if (filtered)
					actor.sendMessage("Added target filter for NPC ID " + npcId);
				else
					actor.sendMessage("Removed target filter for NPC ID " + npcId);
				render(actor);
			}
			return true;
		}
		return false;
	}
	
	public TIntHashSet getFilteredIds()
	{
		return _filteredIds;
	}
	
	public boolean isFilteredId(final int id)
	{
		return _filteredIds.contains(id);
	}

	public boolean toggleFilteredId(final int id)
	{
		if (_filteredIds.contains(id))
		{
			_filteredIds.remove(id);
			return false;
		}
		else
		{
			_filteredIds.add(id);
			return true;
		}
	}
}
