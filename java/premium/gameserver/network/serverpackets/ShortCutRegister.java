package premium.gameserver.network.serverpackets;

import premium.gameserver.model.Player;
import premium.gameserver.model.actor.instances.player.ShortCut;

public class ShortCutRegister extends ShortCutPacket
{
	private ShortcutInfo _shortcutInfo;
	
	public ShortCutRegister(Player player, ShortCut sc)
	{
		this._shortcutInfo = convert(player, sc);
	}
	
	@Override
	protected final void writeImpl()
	{
		this.writeC(0x44);
		
		this._shortcutInfo.write(this);
	}
}