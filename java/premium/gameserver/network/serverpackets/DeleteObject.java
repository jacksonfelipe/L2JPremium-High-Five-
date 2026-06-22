package premium.gameserver.network.serverpackets;

import premium.gameserver.model.GameObject;
import premium.gameserver.model.GameObjectsStorage;
import premium.gameserver.model.Player;

/**
 * Пример: 08 a5 04 31 48 ObjectId 00 00 00 7c unk format d
 */
public class DeleteObject extends L2GameServerPacket
{
	private int _objectId;
	
	public DeleteObject(GameObject obj)
	{
		this._objectId = obj.getObjectId();
	}
	
	@Override
	protected final void writeImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || activeChar.getObjectId() == this._objectId)
		{
			return;
		}
		
		this.writeC(0x08);
		this.writeD(this._objectId);
		this.writeD(0x01); // Что-то странное. Если объект сидит верхом то при 0 он сперва будет ссажен, при 1 просто пропадет.
	}
	
	@Override
	public String getType()
	{
		return super.getType() + " " + GameObjectsStorage.findObject(this._objectId) + " (" + this._objectId + ")";
	}
}