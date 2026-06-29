package premium.gameserver.data.xml.holder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import premium.gameserver.Config;
import premium.gameserver.model.Recipe;
import premium.gameserver.model.RecipeComponent;
import premium.gameserver.templates.StatsSet;
import premium.gameserver.utils.DocumentParser;

import premium.gameserver.Config;
import premium.gameserver.model.Recipe;
import premium.gameserver.model.RecipeComponent;
import premium.gameserver.templates.StatsSet;

public class RecipeHolder extends DocumentParser
{
	private static final Logger _log = LoggerFactory.getLogger(RecipeHolder.class);
	private static RecipeHolder _instance;
	
	private ConcurrentHashMap<Integer, Recipe> _listByRecipeId = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, Recipe> _listByRecipeItem = new ConcurrentHashMap<>();
	
	private RecipeHolder()
	{
		_listByRecipeId = new ConcurrentHashMap<>();
		_listByRecipeItem = new ConcurrentHashMap<>();
		load();
	}
	
	public static RecipeHolder getInstance()
	{
		if (_instance == null)
		{
			_instance = new RecipeHolder();
		}
		return _instance;
	}
	
	@Override
	public void load()
	{
		_listByRecipeId.clear();
		_listByRecipeItem.clear();
		parseDatapackFile("data/recipes.xml");
		_log.info("RecipeController: Loaded " + _listByRecipeId.size() + " Recipes.");
	}
	
	@Override
	protected void parseDocument()
	{
		// Not used without document
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "recipes", d ->
			{
				StatsSet set = parseAttributes(d);
				int id = set.getInteger("id", -1);
				if (id == -1)
				{
					_log.error("Missing id for recipe item, skipping");
					return;
				}
				
				try
				{
					int level = set.getInteger("level");
					int recipeId = set.getInteger("recid");
					String recipeName = set.getString("recipeName");
					int successRate = set.getInteger("successRate");
					int mpCost = set.getInteger("mp");
					int itemId = set.getInteger("itemId");
					int count = set.getInteger("count");
					int foundation = set.getInteger("foundation");
					long exp = set.getLong("exp");
					long sp = set.getLong("sp");
					boolean isDvarvenCraft = set.getBool("dwarven");
					
					Recipe recipeList = new Recipe(id, level, recipeId, recipeName, successRate, mpCost, itemId, foundation, count, exp, sp, isDvarvenCraft);
					
					forEach(d, "recitem", c ->
					{
						StatsSet compSet = parseAttributes(c);
						recipeList.addRecipe(new RecipeComponent(compSet.getInteger("item"), compSet.getInteger("icount")));
					});
					
					_listByRecipeId.put(id, recipeList);
					_listByRecipeItem.put(recipeId, recipeList);
				}
				catch (Exception e)
				{
					_log.error("Missing or invalid attribute for recipe id: " + id + ", skipping", e);
				}
			});
		});
	}
	
	public Collection<Recipe> getRecipes()
	{
		return _listByRecipeId.values();
	}
	
	public Recipe getRecipeByRecipeId(int listId)
	{
		return _listByRecipeId.get(listId);
	}
	
	public Recipe getRecipeByRecipeItem(int itemId)
	{
		return _listByRecipeItem.get(itemId);
	}
}