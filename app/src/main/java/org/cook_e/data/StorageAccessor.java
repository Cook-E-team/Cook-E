/*
 * Copyright 2016 the Cook-E development team
 *
 *  This file is part of Cook-E.
 *
 *  Cook-E is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Cook-E is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.data;

import android.content.Context;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that handles storing and retrieving recipes and bunches from the local database and external database
 */
public class StorageAccessor {
    private SQLAccessor local;
    private SQLAccessor external;
    private StorageParser parser;

    /**
     * Constructor
     * @param c Context of the activity that wants to store/retrieve data
     */
    public StorageAccessor(Context c) {
        parser = new StorageParser();
        local = new SQLiteAccessor(c, parser);
        external = new SQLServerAccessor(parser);
    }

    /**
     * Store a recipe onto the local database
     * @param r Recipe to store
     */
    public void storeRecipe(Recipe r) throws SQLException {
        try {
            local.storeRecipe(r);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Store a bunch onto the local database
     * Assumes that all recipes in the bunch are stored already
     * @param b
     */
    public void storeBunch(Bunch b) throws SQLException {
        try {
            local.storeBunch(b);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Retrieve a recipe from storage
     * @param title String title of the recipe
     * @param author String author of the recipe
     * @return Recipe object or null if recipe could not be found
     */
    public Recipe loadRecipe(String title, String author) throws SQLException {
        try {
            return local.loadRecipe(title, author);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Retrieve a bunch from storage
     * @param name String name of the Bunch
     * @return Bunch object or null if bunch could not be found
     */
    public Bunch loadBunch(String name) throws SQLException {
        try {
            return local.loadBunch(name);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Retrieve all recipes from storage
     * @return List of Recipe objects
     */
    public List<Recipe> loadAllRecipes() throws SQLException {
        List<Recipe> recipes = null;
        try {
            recipes = local.loadAllRecipes();
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return recipes;
    }

    /**
     * Retrieve all bunches from storage
     * @return List of Bunch objects
     */
    public List<Bunch> loadAllBunches() throws SQLException {
        List<Bunch> bunches = null;
        try {
            bunches = local.loadAllBunches();
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return bunches;
    }
    /**
     * Update a recipe on the local database
     * @param r Recipe to update
     */
    public void editRecipe(Recipe r) throws SQLException {
        try {
            local.editRecipe(r);
        } catch (Exception e) {
            throw new SQLException(e);
        }

    }

    /**
     * update a bunch on the local database
     * @param b Bunch to update
     */
    public void editBunch(Bunch b) throws SQLException {
        try {
            local.editBunch(b);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }


    /**
     * delete a recipe on the local database
     * @param r Recipe to delete
     */
    public void deleteRecipe(Recipe r) throws SQLException {
        try {
            local.deleteRecipe(r);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * delete a bunch on the local database
     * @param b Bunch to delete
     */
    public void deleteBunch(Bunch b) throws SQLException {
        try {
            local.deleteBunch(b);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
