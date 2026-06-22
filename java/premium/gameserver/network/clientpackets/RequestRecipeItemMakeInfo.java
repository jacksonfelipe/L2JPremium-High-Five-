package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.RecipeHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.Recipe;
import premium.gameserver.network.serverpackets.RecipeItemMakeInfo;

public class RequestRecipeItemMakeInfo extends L2GameClientPacket
{
	private int _id;
	
	/**
	 * packet type id 0xB7 format: cd
	 */
	@Override
	protected void readImpl()
	{
		this._id = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Recipe recipeList = RecipeHolder.getInstance().getRecipeByRecipeId(this._id);
		if (recipeList == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		
		this.sendPacket(new RecipeItemMakeInfo(activeChar, recipeList, 0xffffffff));
	}
}