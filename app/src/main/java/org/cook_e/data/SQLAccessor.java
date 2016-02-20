package org.cook_e.data;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by kylewoo on 2/19/16.
 */
public interface SQLAccessor {
    public Recipe loadRecipe(String title, String author) throws SQLException;
    public void storeRecipe(Recipe r) throws SQLException;
    public void deleteRecipe(Recipe r) throws SQLException;
    public Bunch loadBunch(String name) throws SQLException;
    public void storeBunch(Bunch b) throws SQLException;
    public void deleteBunch(Bunch b) throws SQLException;
    public void editRecipe(Recipe r) throws SQLException;
    public void editBunch(Bunch b) throws SQLException;
    public List<Recipe> loadAllRecipes() throws SQLException;
    public List<Bunch> loadAllBunches() throws SQLException;
    public List<Recipe> findRecipesLike(String description) throws SQLException;
}
