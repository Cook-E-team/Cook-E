package org.cook_e.data;

import android.support.annotation.NonNull;

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
    public RecipeManager(@NonNull StorageAccessor sa) throws SQLException{
        Objects.requireNonNull(sa, "storage accessor must not be null");
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
    @NonNull
    public List<Recipe> getAllRecipes() {
        return new ArrayList<Recipe>(recipeList);
    }

    /**
     * get titles of all recipes currently in recipe manager
     * titles are in the same order of recipes
     * this list doesn't reflect changes
     * @return list of titles
     */
    @NonNull
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
     * @throws IllegalArgumentException if index i is not valid
     */
    @NonNull
    public Recipe getRecipe(int i) throws IllegalArgumentException{
        indexCheck(i);
        return recipeList.get(i);
    }

    /**
     * add a new recipe to the current recipe list and storage
     * if addition failed, nothing is changed
     * @param r recipe to add
     * @return true on success, false on failure
     */
    public boolean addRecipe(@NonNull Recipe r) {
        Objects.requireNonNull(r, "recipe must not be null");
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
     * @throws IllegalArgumentException if index i is not valid
     */
    public boolean deleteRecipe(int i) throws IllegalArgumentException{
        indexCheck(i);
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
    public boolean deleteRecipe(@NonNull Recipe r) {
        Objects.requireNonNull(r, "recipe must not be null");
        try {
            sa.deleteRecipe(r);
        } catch (SQLException e) {
            return false;
        }
        return recipeList.remove(r);
    }

    /**
     * check if index i is valid for recipe list
     * @param i index to check
     * @throws IllegalArgumentException if index i is not valid
     */
    private void indexCheck(int i) throws IllegalArgumentException{
        if (i < 0 || i >= recipeList.size()) {
            throw new IllegalArgumentException("Index of recipe out of bound.");
        }
    }
}
