package npc.model;

import premium.gameserver.model.Player;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.network.serverpackets.NpcHtmlMessage;
import premium.gameserver.templates.npc.NpcTemplate;

public class RiganInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;
	private static final String FILE_PATH = "custom/";
	
	public RiganInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String fileName = FILE_PATH;
		fileName += getNpcId();
		if (val > 0)
		{
			fileName += "-" + val;
		}
		fileName += ".htm";
		player.sendPacket(new NpcHtmlMessage(player, this, fileName, val));
	}
}
