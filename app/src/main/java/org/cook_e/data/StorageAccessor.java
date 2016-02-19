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
    private static int recipe_counter = 0; // these are local counters, they do not apply to external databases
    private static int bunch_counter = 0;
    private Map<Pair<String, String>, Integer> recipe_ids;
    private Map<String, Integer> bunch_ids;

    /**
     * Constructor
     * @param c Context of the activity that wants to store/retrieve data
     */
    public StorageAccessor(Context c) {
        parser = new StorageParser();
        local = new SQLiteAccessor(c, parser);
        external = new SQLServerAccessor(parser);
        recipe_ids = new HashMap<>();
        bunch_ids = new HashMap<>();
    }

    /**
     * Store a recipe onto the local database
     * @param r Recipe to store
     */
    public void storeRecipe(Recipe r) throws SQLException {
        Integer id = getRecipeId(r);
        if (id == null) {
            id = recipe_counter++;
            recipe_ids.put(new Pair<String, String>(r.getTitle(), r.getAuthor()), id);
        }
        try {
            local.storeRecipe(r, (int) id);
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
        Integer id = getBunchId(b);
        if (id == null) {
            id = bunch_counter++;
            bunch_ids.put(b.getTitle(), id);
        }
        try {
            local.storeBunch(b, (int) id, recipe_ids);
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
        Recipe r = null;
        Integer id = getRecipeId(title, author);
        //if (id == null) // search database
        try {
            r = local.loadRecipe((int) id);
            if (r == null) {
                if (r != null) local.storeRecipe(r, id);
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return r;
    }

    /**
     * Retrieve a bunch from storage
     * @param name String name of the Bunch
     * @return Bunch object or null if bunch could not be found
     */
    public Bunch loadBunch(String name) throws SQLException {
        Integer id = getBunchId(name);
        //if (id == null) // could this happen?
        Bunch b = null;
        try {
            b = local.loadBunch((int) id);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return b;
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
        Integer id = getRecipeId(r);
        try {
            if (id != null) local.editRecipe(r, (int)id);
        } catch (Exception e) {
            throw new SQLException(e);
        }

    }

    /**
     * update a bunch on the local database
     * @param b Bunch to update
     */
    public void editBunch(Bunch b) throws SQLException {
        Integer id = getBunchId(b);
        try {
            if (id != null) {
                local.editBunch(b, (int) id, recipe_ids);
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * delete a recipe on the local database
     * @param title String title of the recipe to delete
     * @param author String author of the recipe to delete
     */
    public void deleteRecipe(String title, String author) throws SQLException {
        Integer id = getRecipeId(title, author);
        try {
            if (id != null) {
                local.deleteRecipe(id);
            }
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
            deleteRecipe(r.getTitle(), r.getAuthor());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * delete a bunch on the local database
     * @param b Bunch to delete
     */
    public void deleteBunch(Bunch b) throws SQLException {
        Integer id = getBunchId(b);
        try {
            local.deleteBunch(id);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Helper that returns id of a recipe
     * @param r Recipe to get id of
     * @return id or null if no id for that recipe exists
     */
    private Integer getRecipeId(Recipe r) {
        return getRecipeId(r.getTitle(), r.getAuthor());
    }

    /**
     * Helper that returns id of a recipe
     * @param title String title of the recipe to get id of
     * @param author String author of the recipe to get id of
     * @return id or null if no id for that recipe exists
     */
    private Integer getRecipeId(String title, String author) {
        Pair<String, String> r_name = new Pair<String, String>(title, author);
        Integer id = recipe_ids.get(r_name);
        return id;
    }

    /**
     * Helper that returns id of a bunch
     * @param b Bunch to get id of
     * @return id or null if no id for that bunch exists
     */
    private Integer getBunchId(Bunch b) {
        return getBunchId(b.getTitle());
    }

    /**
     * Helper that returns id of a bunch
     * @param name String name of the bunch to get id of
     * @return id or null if no id for that bunch exists
     */
    private Integer getBunchId(String name) {
        Integer id = bunch_ids.get(name);
        return id;
    }
}
