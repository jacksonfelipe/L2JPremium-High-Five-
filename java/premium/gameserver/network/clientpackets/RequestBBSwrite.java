package premium.gameserver.network.clientpackets;

import premium.gameserver.Config;
import premium.gameserver.handler.bbs.CommunityBoardManager;
import premium.gameserver.handler.bbs.ICommunityBoardHandler;
import premium.gameserver.model.Player;
import premium.gameserver.network.serverpackets.SystemMessage2;
import premium.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Format SSSSSS
 */
public class RequestBBSwrite extends L2GameClientPacket
{
	private String _url;
	private String _arg1;
	private String _arg2;
	private String _arg3;
	private String _arg4;
	private String _arg5;
	
	@Override
	public void readImpl()
	{
		this._url = this.readS();
		this._arg1 = this.readS();
		this._arg2 = this.readS();
		this._arg3 = this.readS();
		this._arg4 = this.readS();
		this._arg5 = this.readS();
	}
	
	@Override
	public void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(this._url);
		if (handler != null)
		{
			if (!Config.COMMUNITYBOARD_ENABLED)
			{
				activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
			}
			else
			{
				handler.onWriteCommand(activeChar, this._url, this._arg1, this._arg2, this._arg3, this._arg4, this._arg5);
			}
		}
	}
}