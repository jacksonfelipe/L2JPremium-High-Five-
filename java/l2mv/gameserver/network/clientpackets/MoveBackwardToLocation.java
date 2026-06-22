package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.CharMoveToLocation;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Location;

// cdddddd(d)
public class MoveBackwardToLocation extends L2GameClientPacket
{
	private final Location _targetLoc = new Location();
	private final Location _originLoc = new Location();
	private int _moveMovement;
	
	/**
	 * packet type id 0x0f
	 */
	@Override
	protected void readImpl()
	{
		this._targetLoc.x = this.readD();
		this._targetLoc.y = this.readD();
		this._targetLoc.z = this.readD();
		this._originLoc.x = this.readD();
		this._originLoc.y = this.readD();
		this._originLoc.z = this.readD();
		if (this._buf.hasRemaining())
		{
			this._moveMovement = this.readD();
		}
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.setActive();
		
		if (!activeChar.isInObserverMode() && System.currentTimeMillis() - activeChar.getLastMovePacket() < Config.MOVE_PACKET_DELAY)
		{
			activeChar.sendActionFailed();
			return;
		}
		
		activeChar.setLastMovePacket();
		
		if (activeChar.isTeleporting())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.isFrozen())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
			return;
		}
		
		if (activeChar.isInObserverMode())
		{
			if (activeChar.getOlympiadObserveGame() == null)
			{
				activeChar.sendActionFailed();
			}
			else
			{
				activeChar.sendPacket(new CharMoveToLocation(activeChar.getObjectId(), this._originLoc, this._targetLoc));
			}
			
			return;
		}
		
		if (activeChar.isInObserverMode())
		{
			if (activeChar.getOlympiadObserveGame() == null)
			{
				activeChar.sendActionFailed();
			}
			else
			{
				// Synerge - For some reason, the client does movements of 2k when using the keyboard, so we reduce it to something like 200 for each movement
				final int MAX_MOVE_DISTANCE = 200;
				Location targetLoc = this._targetLoc;
				int dx = (this._targetLoc.getX() - this._originLoc.getX());
				int dy = (this._targetLoc.getY() - this._originLoc.getY());
				int dz = (this._targetLoc.getZ() - this._originLoc.getZ());
				double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
				if (distance > MAX_MOVE_DISTANCE)
				{
					final double divider = MAX_MOVE_DISTANCE / distance;
					int x = this._originLoc.getX() + (int) (divider * dx);
					int y = this._originLoc.getY() + (int) (divider * dy);
					int z = this._originLoc.getZ() + (int) (divider * dz);
					// Unless he is trying to move the camera up or down on purpose, we just avoid moving the char among the z
					if (Math.abs(this._originLoc.getZ() - z) <= 50)
					{
						z = this._originLoc.getZ();
					}
					targetLoc = new Location(x, y, z);
				}
				
				activeChar.sendPacket(new CharMoveToLocation(activeChar.getObjectId(), this._originLoc, targetLoc));
			}
			
			return;
		}
		
		if (activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if (activeChar.getTeleMode() > 0)
		{
			if (activeChar.getTeleMode() == 1)
			{
				activeChar.setTeleMode(0);
			}
			activeChar.sendActionFailed();
			activeChar.teleToLocation(this._targetLoc);
			return;
		}
		
		if (activeChar.isInFlyingTransform())
		{
			this._targetLoc.z = Math.min(5950, Math.max(50, this._targetLoc.z)); // В летающей трансформе нельзя летать ниже, чем 0, и выше, чем 6000
		}
		
		// Capture point for AutoFarm Path Editor if in design mode
		l2mv.gameserver.inertiax.model.Inertia inertia = l2mv.gameserver.inertiax.controller.InertiaController.getInstance().getInertia(activeChar);
		if (inertia != null)
		{
			l2mv.gameserver.inertiax.model.InertiaPanel panel = inertia.getPanel(l2mv.gameserver.inertiax.enums.EPanelOption.Path_Editor);
			if (panel instanceof l2mv.gameserver.inertiax.model.panels.PathEditor)
			{
				l2mv.gameserver.inertiax.model.panels.PathEditor pe = (l2mv.gameserver.inertiax.model.panels.PathEditor) panel;
				l2mv.gameserver.inertiax.model.panels.InertiaPath selPath = pe.getSelePath();
				if (selPath != null && selPath.isDesignMode())
				{
					if (selPath.getPoints().size() >= 10)
					{
						activeChar.sendMessage("O caminho ja atingiu o limite de 10 pontos!");
					}
					else
					{
						selPath.addPoint(new Location(this._targetLoc.x, this._targetLoc.y, this._targetLoc.z));
						selPath.renderPath(activeChar);
						selPath.renderPage(activeChar);
						activeChar.sendMessage("Ponto " + (char)('A' + selPath.getPoints().size() - 1) + " adicionado no clique.");
					}
				}
			}
		}
		
		activeChar.moveToLocation(this._targetLoc, 0, this._moveMovement != 0 && !activeChar.getVarB("no_pf"));
	}
}