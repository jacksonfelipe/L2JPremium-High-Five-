package premium.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import premium.gameserver.instancemanager.CursedWeaponsManager;
import premium.gameserver.model.Creature;
import premium.gameserver.model.CursedWeapon;
import premium.gameserver.network.serverpackets.ExCursedWeaponLocation;
import premium.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;
import premium.gameserver.utils.Location;

public class RequestCursedWeaponLocation extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		Creature activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		List<CursedWeaponInfo> list = new ArrayList<CursedWeaponInfo>();
		for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			Location pos = cw.getWorldPosition();
			if (pos != null)
			{
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
			}
		}
		
		activeChar.sendPacket(new ExCursedWeaponLocation(list));
	}
}