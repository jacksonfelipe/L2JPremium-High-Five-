package premium.gameserver.inertiax.model.panels;

import premium.gameserver.inertiax.model.Inertia;
import premium.gameserver.inertiax.model.InertiaPanel;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;
	
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
