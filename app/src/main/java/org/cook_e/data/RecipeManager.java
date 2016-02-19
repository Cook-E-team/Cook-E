package org.cook_e.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps and manages all saved recipes in the app
 */
public class RecipeManager {
    private List<Recipe> recipeList;
    private StorageAccessor sa;

    /**
     * constructor
     * @param sa StorageAccessor to access storage
     * @throws SQLException when an error happened in reading recipes
     */
    RecipeManager(StorageAccessor sa) throws SQLException{
        this.sa = sa;
        try {
            recipeList = sa.loadAllRecipes();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * get all recipes currently in recipe manager
     * return a copy of current recipes, doesn't reflect changes
     * @return list of recipes
     */
    public List<Recipe> getAllRecipes() {
        return new ArrayList<Recipe>(recipeList);
    }

    /**
     * get titles of all recipes currently in recipe manager
     * titles are in the same order of recipes
     * this list doesn't reflect changes
     * @return list of titles
     */
    public List<String> getAllRecipeTitles() {
        List<String> res = new ArrayList<String>();
        for (int i = 0; i < recipeList.size(); i++) {
            res.add(recipeList.get(i).getTitle());
        }
        return res;
    }

    /**
     * get the ith recipe in the current recipe list
     * @param i index of the recipe, index start from 0
     * @return recipe at ith position
     */
    public Recipe getRecipe(int i) {
        return recipeList.get(i);
    }

    /**
     * add a new recipe to the current recipe list and storage
     * if addition failed, nothing is changed
     * @param r recipe to add
     * @return true on success, false on failure
     */
    public boolean addRecipe(Recipe r) {
        try {
            sa.storeRecipe(r);
        } catch (SQLException e) {
            return false;
        }
        recipeList.add(r);
        return true;
    }

    /**
     * delete recipe in position i of the current recipe list
     * if deletion failed, nothing is changed
     * @param i index of recipe to remove
     * @return true on success, false on failure
     */
    public boolean deleteRecipe(int i) {
        Recipe r = recipeList.get(i);
        try {
            sa.deleteRecipe(r);
        } catch (SQLException e) {
            return false;
        }
        recipeList.remove(i);
        return true;
    }

    /**
     * delete recipe r from current recipe list, if present
     * if deletion failed, nothing is changed
     * @param r recipe to delete
     * @return true on success, false on failure
     */
    public boolean deleteRecipe(Recipe r) {
        try {
            sa.deleteRecipe(r);
        } catch (SQLException e) {
            return false;
        }
        return recipeList.remove(r);
    }
}
