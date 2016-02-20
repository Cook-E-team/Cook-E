package org.cook_e.data;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * An interface for classes that can access a database to store recipes and bunches
 */
public interface SQLAccessor {
    /**
     * Locates and returns a recipe with the requested title and author
     * @param title the recipe title (must not be null)
     * @param author the recipe author (must not be null)
     * @return a Recipe with the requested title and author, or null if none could be found
     * @throws SQLException if an error occurs
     */
    Recipe loadRecipe(String title, String author) throws SQLException;

    /**
     * Inserts a recipe into the database
     * @param r the recipe to insert. Must not have an object ID.
     * @throws SQLException if an error occurs
     */
    void storeRecipe(Recipe r) throws SQLException;

    /**
     * Deletes a recipe from the database
     * @param r the recipe to delete. Must have an object ID.
     * @throws SQLException if an error occurs
     */
    void deleteRecipe(Recipe r) throws SQLException;

    /**
     * Locates and returns a bunch with the requested name
     * @param name the bunch name (must not be null)
     * @return a Bunch with the requested name, or null if none could be found
     * @throws SQLException if an error occurs
     */
    Bunch loadBunch(String name) throws SQLException;

    /**
     * Inserts a bunch into the database
     * @param b the bunch to insert. Must not have an object ID.
     * @throws SQLException if an error occurs
     */
    void storeBunch(Bunch b) throws SQLException;

    /**
     * Deletes a bunch from the database
     * @param b the bunch to delete. Must have an object ID.
     * @throws SQLException if an error occurs
     */
    void deleteBunch(Bunch b) throws SQLException;

    /**
     * Updates a recipe that has already been inserted into the database
     * @param r The recipe to update. Must have an object ID.
     * @throws SQLException if an error occurs
     */
    void editRecipe(Recipe r) throws SQLException;

    /**
     * Updates a bunch that has already been inserted into the database
     * @param b the bunch to update. Must have an object ID.
     * @throws SQLException if an error occurs
     */
    void editBunch(Bunch b) throws SQLException;

    /**
     * Selects and returns all recipes in the database
     * @return a list of recipes
     * @throws SQLException if an error occurs
     */
    List<Recipe> loadAllRecipes() throws SQLException;

    /**
     * Selects and returns all bunches in the database
     * @return a list of bunches
     * @throws SQLException if an error occurs
     */
    List<Bunch> loadAllBunches() throws SQLException;

    // TODO: Document this method
    List<Recipe> findRecipesLike(String description) throws SQLException;
}
