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

import java.util.Map;

/**
 * Class that handles storing and retrieving recipes and bunches from the local sqlite database and external sqlserver database
 */
public class StorageAccessor {
    private SQLiteAccessor sqlite;
    private SQLServerAccessor sqlserver;
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
        sqlite = new SQLiteAccessor(c, parser);
        sqlserver = new SQLServerAccessor(parser);
        parser = new StorageParser();
    }

    /**
     * Store a recipe onto the local sqlite database
     * @param r Recipe to store
     */
    public void storeRecipe(Recipe r) {
        Integer id = getRecipeId(r);
        if (id == null) recipe_ids.put(new Pair<String, String>(r.getTitle(), r.getAuthor()), recipe_counter++);
        sqlite.storeRecipe(r, (int)id);
    }

    /**
     * Store a bunch onto the local sqlite database
     * Assumes that all recipes in the bunch are stored already
     * @param b
     */
    public void storeBunch(Bunch b) {
        Integer id = getBunchId(b);
        if (id == null) bunch_ids.put(b.getTitle(), bunch_counter++);
        sqlite.storeBunch(b, (int) id, recipe_ids);
    }

    /**
     * 
     * @param title
     * @param author
     * @return
     */
    public Recipe loadRecipe(String title, String author) {
        Recipe r = null;
        Integer id = getRecipeId(title, author);
        //if (id == null) // search database
        r = sqlite.loadRecipe((int)id);
        if (r == null) {
            //r = sqlserver.findRecipe(title, author); // this section will be expanded when database is implemented
            if (r != null) sqlite.storeRecipe(r, id);
        }
        return r;
    }
    public Bunch loadBunch(String name) {
        Integer id = getBunchId(name);
        //if (id == null) // could this happen?
        Bunch b = sqlite.loadBunch((int)id);
        return b;
    }
    public void editRecipe(Recipe r) {
        Integer id = getRecipeId(r);
        if (id != null) sqlite.editRecipe(r, (int)id);

    }
    public void editBunch(Bunch b) {
        Integer id = getBunchId(b);
        if (id != null) {
            sqlite.editBunch(b, (int)id, recipe_ids);
        }
    }
    public void deleteRecipe(String title, String author) {
        Integer id = getRecipeId(title, author);
        if (id != null) {
            sqlite.deleteRecipe(id);
        }
    }
    public void deleteRecipe(Recipe r) {
        deleteRecipe(r.getTitle(), r.getAuthor());
    }
    public void deleteBunch(Bunch b) {
        Integer id = getBunchId(b);
        sqlite.deleteBunch(id);
    }
    private Integer getRecipeId(Recipe r) {
        return getRecipeId(r.getTitle(), r.getAuthor());
    }
    private Integer getRecipeId(String title, String author) {
        Pair<String, String> r_name = new Pair<String, String>(title, author);
        Integer id = recipe_ids.get(r_name);
        return id;
    }
    private Integer getBunchId(Bunch b) {
        return getBunchId(b.getTitle());
    }
    private Integer getBunchId(String name) {
        Integer id = bunch_ids.get(name);
        return id;
    }
}
