package premium.gameserver.network.clientpackets;

import premium.gameserver.data.xml.holder.RecipeHolder;
import premium.gameserver.model.Player;
import premium.gameserver.model.Recipe;
import premium.gameserver.network.serverpackets.RecipeBookItemList;

public class RequestRecipeItemDelete extends L2GameClientPacket
{
	private int _recipeId;
	
	@Override
	protected void readImpl()
	{
		this._recipeId = this.readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
		{
			activeChar.sendActionFailed();
			return;
		}
		
		Recipe rp = RecipeHolder.getInstance().getRecipeByRecipeId(this._recipeId);
		if (rp == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		
		activeChar.unregisterRecipe(this._recipeId);
		activeChar.sendPacket(new RecipeBookItemList(activeChar, rp.isDwarvenRecipe()));
	}
}