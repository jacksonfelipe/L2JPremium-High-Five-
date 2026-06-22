package l2mv.gameserver.inertiax.model.panels;

import l2mv.gameserver.inertiax.model.Inertia;
import l2mv.gameserver.inertiax.model.InertiaPanel;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
	
public class RangeEditor extends InertiaPanel
{
	public RangeEditor(final Inertia inertia)
	{
		super(inertia);
	}

	@Override
	public void render(final Player viewer)
	{
		final var player = _inertia.getActivePlayer();
		if (player == null)
			return;
		
		final var npcHtml = new NpcHtmlMessage(5);
		npcHtml.setFile("mods/autofarm/rangeeditor.htm");

		npcHtml.replace("%id%", String.valueOf(_ownerId));
		
		player.sendPacket(npcHtml);
	}
	
}
