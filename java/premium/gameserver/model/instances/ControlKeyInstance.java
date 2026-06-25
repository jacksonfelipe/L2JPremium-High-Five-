package premium.gameserver.model.instances;

import premium.commons.lang.reference.HardReference;
import premium.gameserver.idfactory.IdFactory;
import premium.gameserver.model.GameObject;
import premium.gameserver.model.Player;
import premium.gameserver.model.reference.L2Reference;
import premium.gameserver.network.serverpackets.MyTargetSelected;

public class ControlKeyInstance extends GameObject
{
	private static final long serialVersionUID = 1L;
	protected HardReference<ControlKeyInstance> reference;
	
	public ControlKeyInstance()
	{
		super(IdFactory.getInstance().getNextId());
		reference = new L2Reference<>(this);
	}
	
	@Override
	public HardReference<ControlKeyInstance> getRef()
	{
		return reference;
	}
	
	@Override
	public void onAction(Player player, boolean shift)
	{
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
			return;
		}
		
		player.sendActionFailed();
	}
}
