package premium.gameserver.network.clientpackets;

import premium.gameserver.cache.ItemInfoCache;
import premium.gameserver.model.Player;
import premium.gameserver.model.items.ItemInfo;
import premium.gameserver.network.serverpackets.ActionFail;
import premium.gameserver.network.serverpackets.ExRpItemLink;

public class RequestExRqItemLink extends L2GameClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		ItemInfo item;
		if ((item = ItemInfoCache.getInstance().get(this._objectId)) == null)
		{
			// Nik: Support for question mark listeners. Used for party find and other shits. objectId is used as the questionMarkId. Use with caution.
			this.getClient().getActiveChar().getListeners().onQuestionMarkClicked(this._objectId);
			
			if (this._objectId >= 5000000 && this._objectId < 6000000)
			{
				Player player = this.getClient().getActiveChar();
				String varName = "DisabledAnnounce" + this._objectId;
				if (!player.containsQuickVar(varName))
				{
					player.addQuickVar(varName, "true");
					player.sendMessage("Announcement Disabled!");
				}
			}
			this.sendPacket(ActionFail.STATIC);
		}
		else
		{
			this.sendPacket(new ExRpItemLink(item));
		}
	}
}