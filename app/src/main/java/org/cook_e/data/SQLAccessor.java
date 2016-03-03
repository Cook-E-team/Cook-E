/*
 * Copyright 2016 the Cook-E development team
 *
 * This file is part of Cook-E.
 *
 * Cook-E is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cook-E is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.data;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * An interface for classes that can access a database to store recipes and bunches
 */
public interface SQLAccessor extends Closeable {
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
     * @param limit int limit of number of recipes to load (loads all if -1)
     * @return a list of recipes
     * @throws SQLException if an error occurs
     */
    List<Recipe> loadAllRecipes(int limit) throws SQLException;

    /**
     * Selects and returns all bunches in the database
     * @param limit int limit of number of bunches to load (loads all if -1)
     * @return a list of bunches
     * @throws SQLException if an error occurs
     */
    List<Bunch> loadAllBunches(int limit) throws SQLException;

    /**
     * Searches for and returns recipes that contain the provides string in their titles
     * @param title the title to search for
     * @return a list of matching recipes
     * @throws SQLException if an error occurrs
     */
    List<Recipe> findRecipesLike(String title) throws SQLException;

    /**
     * Checks the representation invariants of the database schema
     * @throws SQLException if an invariant is not met
     */
    void checkInvariants() throws SQLException;

    /**
     * Removes all rows from all tables in this database
     * @throws SQLException if an error occurs
     */
    void clearAllTables() throws SQLException;

    /**
     * Checks if this database contains a recipe with the specified ID
     * @param id the id to check
     * @return true if this database contains a recipe with the specified ID, otherwise false
     */
    boolean containsRecipe(long id) throws SQLException;

    void storeLearnerData(Recipe r, Collection<LearningWeight> weights) throws SQLException;

    void updateLearnerData(Recipe r, LearningWeight weight) throws SQLException;
    List<LearningWeight> loadLearnerData(Recipe r) throws SQLException;
    void deleteLearnerData() throws SQLException;
}
