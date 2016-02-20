package org.cook_e.data;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by kylewoo on 2/19/16.
 */
public interface SQLAccessor {
    public Recipe loadRecipe(int id) throws SQLException;
    public void storeRecipe(Recipe r, int id) throws SQLException;
    public void deleteRecipe(int id) throws SQLException;
    public Bunch loadBunch(int id) throws SQLException;
    public void storeBunch(Bunch b, int bunch_id, Map<Pair<String, String>, Integer> recipe_ids) throws SQLException;
    public void deleteBunch(int id) throws SQLException;
    public void editRecipe(Recipe r, int id) throws SQLException;
    public void editBunch(Bunch b, int bunch_id, Map<Pair<String, String>, Integer> recipe_ids) throws SQLException;
    public List<Recipe> loadAllRecipes() throws SQLException;
    public List<Bunch> loadAllBunches() throws SQLException;
    public List<Recipe> findRecipesLike(String description) throws SQLException;
}
