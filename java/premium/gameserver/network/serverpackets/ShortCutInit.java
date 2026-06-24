package premium.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import premium.gameserver.model.Player;
import premium.gameserver.model.actor.instances.player.ShortCut;

public class ShortCutInit extends ShortCutPacket
{
	private List<ShortcutInfo> _shortCuts = Collections.emptyList();
	
	public ShortCutInit(Player pl)
	{
		Collection<ShortCut> shortCuts = pl.getAllShortCuts();
		this._shortCuts = new ArrayList<>(shortCuts.size());
		for (ShortCut shortCut : shortCuts)
		{
			this._shortCuts.add(convert(pl, shortCut));
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeC(0x45);
		this.writeD(this._shortCuts.size());
		
		for (ShortcutInfo sc : this._shortCuts)
		{
			sc.write(this);
		}
	}
}