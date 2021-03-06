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

import android.content.Context;
import android.util.Pair;


import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that handles storing and retrieving recipes and bunches from the local database and
 * external database
 *
 * Objects that may be stored in the database can have object IDs, as specified by the
 * {@link DatabaseObject} class. Newly created objects have no ID. When an object is inserted,
 * the insert method changes the ID of the inserted object to match the ID of the entry in
 * the database.
 */
public class StorageAccessor implements Closeable {

    /**
     * The local database accessor
     */
    private SQLAccessor mLocal;
    /**
     * The remote database accessor
     */
    private SQLAccessor mExternal;

    /**
     * Map used to cache recipes
     * maps id to recipe
     */
    private Map<String, Recipe> mRecipeBuffer;


    /**
     * Map used to cache bunches
     * maps id to bunch
     */
    private Map<String, Bunch> mBunchBuffer;

    /**
     * constant size limit for the buffers
     */
    private static final int BUFFER_LIMIT = 10;
    /**
     * Constructor
     * @param c Context of the activity that wants to store/retrieve data
     */
    public StorageAccessor(Context c) throws SQLException {
        final StorageParser parser = new StorageParser();
        mLocal = new SQLiteAccessor(c, parser);
        mExternal = new SQLServerAccessor(parser);
        mRecipeBuffer = new HashMap<>();
        mBunchBuffer = new HashMap<>();
    }

    /**
     * Store a recipe onto the local database
     *
     * @param r Recipe to store
     * @throws IllegalArgumentException if the provided recipe already has an object ID
     */
    public void storeRecipe(Recipe r) throws SQLException {
        if (r.getObjectId() != DatabaseObject.NO_ID && mLocal.containsRecipe(r.getObjectId())) {
            throw new IllegalArgumentException("Recipe has already been stored");
        }
        mLocal.storeRecipe(r);
        updateRecipeBuffer(r);
    }

    /**
     * Store a bunch onto the local database
     * Assumes that all recipes in the bunch are stored already
     * @param b the bunch to store
     * @throws IllegalArgumentException if the provided bunch already has an object ID
     */
    public void storeBunch(Bunch b) throws SQLException {
        if (b.hasObjectId()) {
            throw new IllegalArgumentException("Bunch has already been stored");
        }
        mLocal.storeBunch(b);
    }

    /**
     * Find recipes from external storage based on keyword
     * @param keyword String to search on
     * @return List of Recipes containing keyword in their title
     * @throws SQLException
     */
    public List<Recipe> loadRecipes(String keyword) throws SQLException {
        List<Recipe> recipes = mExternal.findRecipesLike(keyword);



        return recipes;
    }
    /**
     * Retrieve a recipe from storage
     *
     * If recipes are found in both the local and remote databases, the recipe from the local
     * database will be returned.
     *
     * @param title String title of the recipe
     * @param author String author of the recipe
     * @return Recipe object or null if recipe could not be found
     */
    public Recipe loadRecipe(String title, String author) throws SQLException {
        final Recipe local = mLocal.loadRecipe(title, author);
        if (local == null) {
            return mExternal.loadRecipe(title, author);
        } else {
            return local;
        }
    }

    /**
     * Retrieve a bunch from storage
     *
     * @param name String name of the Bunch
     * @return Bunch object or null if bunch could not be found
     */
    public Bunch loadBunch(String name) throws SQLException {
        return mLocal.loadBunch(name);
    }

    /**
     * Retrieve all recipes from storage
     *
     * @return List of Recipe objects
     */
    public List<Recipe> loadAllRecipes(int limit) throws SQLException {
        final Set<Recipe> recipeSet = new HashSet<>();
        recipeSet.addAll(mLocal.loadAllRecipes(limit));
        //recipeSet.addAll(mExternal.loadAllRecipes());
        return new ArrayList<>(recipeSet);
    }

    /**
     * Retrieve all bunches from storage
     * @return List of Bunch objects
     */
    public List<Bunch> loadAllBunches(int limit) throws SQLException {
        return mLocal.loadAllBunches(limit);
    }
    /**
     * Update a recipe on the local database
     * @param r Recipe to update
     * @throws IllegalArgumentException if the recipe has not been stored in this database
     */
    public void editRecipe(Recipe r) throws SQLException {
        if (!r.hasObjectId()) {
            throw new IllegalArgumentException("Recipe has not been stored");
        }
        mLocal.editRecipe(r);
    }

    /**
     * Updates or inserts a recipe, depending on whether the recipe has an object ID set
     *
     * If the recipe has no object ID, it will be assigned one.
     *
     * @param r the recipe to persist
     * @throws SQLException if an error occurs
     */
    public void persistRecipe(Recipe r) throws SQLException {
        if (r.hasObjectId()) {
            editRecipe(r);
        } else {
            storeRecipe(r);
        }
    }

    /**
     * update a bunch on the local database
     * @param b Bunch to update
     * @throws IllegalArgumentException if the bunch has not been stored in this database
     */
    public void editBunch(Bunch b) throws SQLException {
        if (!b.hasObjectId()) {
            throw new IllegalArgumentException("Bunch has not been stored");
        }
        mLocal.editBunch(b);
    }

    /**
     * Updates or inserts a bunch, depending on whether the bunch has an object ID set
     *
     * If the bunch has no object ID, it will be assigned one.
     *
     * @param b the bunch to persist
     * @throws SQLException if an error occurs
     */
    public void persistBunch(Bunch b) throws SQLException {
        if (b.hasObjectId()) {
            editBunch(b);
        } else {
            storeBunch(b);
        }
    }


    /**
     * delete a recipe on the local database
     * @param r Recipe to delete
     * @throws IllegalArgumentException if the recipe has not been stored in this database
     */
    public void deleteRecipe(Recipe r) throws SQLException {
        if (!r.hasObjectId()) {
            throw new IllegalArgumentException("Recipe has not been stored");
        }
        mLocal.deleteRecipe(r);
    }

    /**
     * delete a bunch on the local database
     * @param b Bunch to delete
     * @throws IllegalArgumentException if the bunch has not been stored in this database
     */
    public void deleteBunch(Bunch b) throws SQLException {
        if (!b.hasObjectId()) {
            throw new IllegalArgumentException("Bunch has not been stored");
        }
        mLocal.deleteBunch(b);
    }

    /**
     * Determines if the local database has a recipe with the provided ID
     * @param id the ID to check
     * @return true if the local database has a recipe with the provided ID, otherwise false
     * @throws SQLException if an error occurs
     */
    public boolean containsLocalRecipe(long id) throws SQLException {
        return mLocal.containsRecipe(id);
    }

    /**
     * Store the leanrner data into the local dataabase
     * @param r Recipe related to the learner
     * @param weights Learning weights
     * @throws SQLException
     */
    public void storeLearnerData(Recipe r, Collection<LearningWeight> weights) throws SQLException {
        mLocal.storeLearnerData(r, weights);
    }

    /**
     * Load learner data for a specific recipe.
     * @param r the recipe of concern
     * @return the loaded learner data for the recipe passed in
     * @throws SQLException
     */
    public List<LearningWeight> loadLearnerData(Recipe r) throws SQLException {
        return mLocal.loadLearnerData(r);
    }

    /**
     * Update learner data for a recipe
     * @param r Recipe to update learner data for
     * @param weight LearningWeight that is the updated data
     * @throws SQLException if an error occurs
     */
    public void updateLearnerData(Recipe r, LearningWeight weight) throws SQLException {
        mLocal.updateLearnerData(r, weight);
    }

    /**
     * Warning, this deletes all learner data from the database
     * @throws SQLException if an error occurs
     */
    public void deleteLearnerData() throws SQLException {
        mLocal.deleteLearnerData();
    }

    /**
     * Loads learner data for a bunch
     * @param b Bunch to load learner data for
     * @return Map of recipe ids to LearningWeights
     * @throws SQLException
     */
    public Map<Long, List<LearningWeight>> loadLearnerData(Bunch b) throws SQLException {
        Map<Long, List<LearningWeight>> result = new HashMap<>();
        for (Recipe r : b.getRecipes()) {
            result.put(r.getObjectId(), loadLearnerData(r));
        }
        return result;
    }

    /**
     * Helper that updates the recipe buffer, adhering to size constraints
     * @param r
     */
    private void updateRecipeBuffer(Recipe r) {

    }
    @Override
    public void close() throws IOException {
        mLocal.close();
        mExternal.close();
    }
}
