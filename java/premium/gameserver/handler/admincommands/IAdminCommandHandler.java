package premium.gameserver.handler.admincommands;

import premium.gameserver.model.Player;

public interface IAdminCommandHandler
{
 
	@SuppressWarnings("rawtypes")
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar);
	
	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	@SuppressWarnings("rawtypes")
	public Enum[] getAdminCommandEnum();
}