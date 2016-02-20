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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class implements methods allowing for storage and access to an android local sqlite database
 */
public class SQLiteAccessor implements SQLAccessor {
    /**
     * Parser for transforming a string description into a Recipe and vice versa
     */
    private StorageParser parser;
    /**
     * Helper that has methods for accessing the android local sqlite database
     */
    private RecipeOpenHelper helper;

    /**
     * Constants for use in creating and accessing columns for the tables
     */
    private static final String DATABASE_NAME = "RecipesDatabase";
    private static final String RECIPE_TABLE_NAME = "Recipes";
    private static final String[] RECIPE_COLUMNS = {"id", "name", "author", "description"}; // note the indexes are 0 based
    private static final String RECIPE_IMAGE_TABLE_NAME = "RecipeImages";
    private static final String[] RECIPE_IMAGE_COLUMNS = {"recipe_id", "image"};
    private static final String BUNCH_TABLE_NAME = "Bunches";
    private static final String[] BUNCH_COLUMNS = {"id", "name"};
    private static final String BUNCH_RECIPES_TABLE_NAME = "BunchRecipes";
    private static final String[] BUNCH_RECIPE_COLUMNS = {"bunch_id", "recipe_id"};
    /**
     * Schema of the Recipes table: (id, name, author, description)
     */
    private static final String RECIPE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_TABLE_NAME + " (" +
                    RECIPE_COLUMNS[0] + " INTEGER PRIMARY KEY," +
                    RECIPE_COLUMNS[1]  +" TEXT NOT NULL DEFAULT \"\"," +
                    RECIPE_COLUMNS[2]  + " TEXT NOT NULL DEFAULT \"\"," +
                    RECIPE_COLUMNS[3]  + " TEXT NOT NULL DEFAULT \"\");";
    /**
     * Schema of the Recipe Images table: (id, image)
     */
    private static final String RECIPE_IMAGE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_IMAGE_TABLE_NAME + " (" +
                    RECIPE_IMAGE_COLUMNS[0] + " INTEGER NOT NULL," +
                    RECIPE_IMAGE_COLUMNS[1] + " BLOB NOT NULL," +
                    " PRIMARY KEY (" + RECIPE_IMAGE_COLUMNS[0] + ", " + RECIPE_IMAGE_COLUMNS[1] + "));";
    /**
     * Schema of the Bunches table: (id, name)
     */
    private static final String BUNCH_TABLE_CREATE =
            "CREATE TABLE " + BUNCH_TABLE_NAME + " (" +
                    BUNCH_COLUMNS[0] + " INTEGER PRIMARY KEY," +
                    BUNCH_COLUMNS[1] + " TEXT NOT NULL DEFAULT \"\");";
    /**
     * Schema of the Bunch Recipes table: (bunch_id, recipe_id)
     */
    private static final String BUNCH_RECIPE_TABLE_CREATE =
            "CREATE TABLE " + BUNCH_RECIPES_TABLE_NAME + " (" +
                    BUNCH_RECIPE_COLUMNS[0] + " INT NOT NULL DEFAULT 0," +
                    BUNCH_RECIPE_COLUMNS[1] + " INT NOT NULL DEFAULT 0," +
                    " PRIMARY KEY (" + BUNCH_RECIPE_COLUMNS[0] + ", " + BUNCH_RECIPE_COLUMNS[1] + "));";

    /**
     * Constructor
     * @param c Context of the activity that will be using the sqlite database
     * @param parser Parser that will implement String > Recipe and Recipe > String transformation
     */
    public SQLiteAccessor(Context c, StorageParser parser) {
        helper = new RecipeOpenHelper(c);
        this.parser = parser;
    }

    /**
     * Store a recipe on the sqlite database
     * @param r Recipe object to store
     * @param id int id of the recipe
     */
    @Override
    public void storeRecipe(Recipe r) throws SQLException {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = createContentValues(r);
            db.insert(RECIPE_TABLE_NAME, null, values);
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Store a bunch on the sqlite database
     * Assumes that all recipes within this bunch have been stored on the database
     * @param b Bunch object to store
     * @param bunch_id int id of the bunch
     * @param recipe_ids Map of (recipe name, recipe author) to recipe ids
     */
    @Override
    public void storeBunch(Bunch b) throws SQLException {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            List<ContentValues> values_list = createContentValues(b, bunch_id, recipe_ids);
            ContentValues bunch_values = createContentValues(b, bunch_id);
            db.insert(BUNCH_TABLE_NAME, null, bunch_values);
            for (ContentValues values: values_list) {
                db.insert(BUNCH_RECIPES_TABLE_NAME, null, values);
            }
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Edit a recipe stored on the sqlite database
     * @param r Recipe object to update
     * @param id int id of recipe
     */
    @Override
    public void editRecipe(Recipe r, int id) throws SQLException {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = createContentValues(r, id);
            String[] whereArgs = {r.getTitle(), r.getAuthor()};
            db.update(RECIPE_TABLE_NAME, values, "id = ?", whereArgs);
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Edit a bunch stored on the sqlite database
     * @param b Bunch object to edit
     * @param bunch_id int id of bunch
     * @param recipe_ids Map of (recipe name, recipe author) to recipe id
     */
    @Override
    public void editBunch(Bunch b, int bunch_id, Map<Pair<String, String>, Integer> recipe_ids) throws SQLException {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues bunch_values = createContentValues(b, bunch_id);
            String[] bunchArgs = {String.valueOf(bunch_id)};
            db.update(BUNCH_TABLE_NAME, bunch_values, "id = ?", bunchArgs);
            db.delete(BUNCH_RECIPES_TABLE_NAME, "bunch_id = ?", bunchArgs);
            List<ContentValues> bunch_recipe_values = createContentValues(b, bunch_id, recipe_ids);
            for (ContentValues cv: bunch_recipe_values) {
                db.insert(BUNCH_RECIPES_TABLE_NAME, null, cv);
            }
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Load a recipe off the database
     * @param id int id of the recipe
     * @return Recipe object
     */
    @Override
    public Recipe loadRecipe(int id) throws SQLException {
        Recipe r = null;

        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            String[] whereArgs = {String.valueOf(id)};
            Cursor c = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, "id = ?", whereArgs,
                    null, null, "name");
            if (c != null) {
                c.moveToFirst();
                String title = c.getString(1);
                String author = c.getString(2);
                String description = c.getString(3);
                r = parser.convertStringToRecipe(title, author, description);
                c.close();
            }
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }

        return r;
    }

    /**
     * Load all recipes off the database
     * @return List of Recipes
     */
    @Override
    public List<Recipe> loadAllRecipes() throws SQLException {

        List<Recipe> recipes = new ArrayList<Recipe>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, null, null, null, null, "name");
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {

                    String title = c.getString(1);
                    String author = c.getString(2);
                    String description = c.getString(3);
                    Recipe r = parser.convertStringToRecipe(title, author, description);
                    recipes.add(r);
                } while (c.moveToNext());
                c.close();
            }
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return recipes;
    }

    /**
     * Not used for this sqlite accessor
     * @param description
     * @return
     */
    @Override
    public List<Recipe> findRecipesLike(String description) { return null; }
    /**
     * Load all bunches off the database
     * @return List of bunches
     */
    @Override
    public List<Bunch> loadAllBunches() throws SQLException {
        List<Bunch> bunches = new ArrayList<Bunch>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query(BUNCH_TABLE_NAME, BUNCH_COLUMNS, null, null, null, null, "name");
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    List<Recipe> recipes = new ArrayList<Recipe>();
                    int bunch_id = c.getInt(0);
                    String name = c.getString(1);
                    String[] whereArgs = {String.valueOf(bunch_id)};
                    Cursor recipe_bunch_cursor = db.query(BUNCH_RECIPES_TABLE_NAME, BUNCH_RECIPE_COLUMNS, "bunch_id = ?", whereArgs,
                            null, null, null);
                    if (recipe_bunch_cursor.getCount() > 0) {
                        recipe_bunch_cursor.moveToFirst();
                        do {
                            int recipe_id = recipe_bunch_cursor.getInt(1);
                            String[] recipeWhereArgs = {String.valueOf(recipe_id)};
                            Cursor recipe_cursor = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, "id = ?", recipeWhereArgs,
                                    null, null, null);
                            Recipe r = null;
                            String title = recipe_cursor.getString(1);
                            String author = recipe_cursor.getString(2);
                            String description = recipe_cursor.getString(3);
                            r = parser.convertStringToRecipe(title, author, description);
                            recipes.add(r);
                            recipe_cursor.close();
                        } while (recipe_bunch_cursor.moveToNext());
                        recipe_bunch_cursor.close();
                    }
                    Bunch b = new Bunch(name, recipes);
                    bunches.add(b);
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return bunches;

    }
    /**
     * Load a bunch off the database (and all its contained recipes)
     * @param id
     * @return Bunch object
     */
    @Override
    public Bunch loadBunch(int id) throws SQLException {
        Bunch b = null;
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            String[] whereArgs = {String.valueOf(id)};
            Cursor c = db.query(BUNCH_TABLE_NAME, BUNCH_COLUMNS, "id = ?", whereArgs,
                    null, null, "name");
            List<Recipe> recipes = new ArrayList<Recipe>();
            if (c != null) {
                c.moveToFirst();
                String name = c.getString(1);
                Cursor recipe_bunch_cursor = db.query(BUNCH_RECIPES_TABLE_NAME, BUNCH_RECIPE_COLUMNS,
                        "bunch id = ?", whereArgs,
                        null, null, null);
                if (recipe_bunch_cursor != null) {
                    recipe_bunch_cursor.moveToFirst();
                    do {
                        int recipe_id = recipe_bunch_cursor.getInt(1);
                        String[] recipeWhereArgs = {String.valueOf(recipe_id)};
                        Cursor recipe_cursor = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, "id = ?", recipeWhereArgs,
                                null, null, null);
                        Recipe r = null;
                        String title = recipe_cursor.getString(1);
                        String author = recipe_cursor.getString(2);
                        String description = recipe_cursor.getString(3);
                        r = parser.convertStringToRecipe(title, author, description);
                        recipes.add(r);
                        recipe_cursor.close();
                    } while (recipe_bunch_cursor.moveToNext());
                    recipe_bunch_cursor.close();
                    b = new Bunch(name, recipes);
                }
                c.close();
            }
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return b;
    }

    /**
     * Delete a recipe from the database
     * @param id int id of recipe
     */
    @Override
    public void deleteRecipe(int id) throws SQLException {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String[] whereArgs = {String.valueOf(id)};
            db.delete(RECIPE_TABLE_NAME, "id = ?", whereArgs);
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Delete a bunch from the database
     * @param id int id of bunch
     */
    @Override
    public void deleteBunch(int id) throws SQLException {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String[] whereArgs = {String.valueOf(id)};
            db.delete(BUNCH_TABLE_NAME, "id = ?", whereArgs);
            db.delete(BUNCH_RECIPES_TABLE_NAME, "bunch_id = ?", whereArgs);
            db.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Helper that creates a ContentValues object for the Recipes table
     * @param r Recipe object to take values from
     * @param id int id of recipe
     * @return ContentValues containing the mapping from column name to value for Recipes Table
     */
    private ContentValues createContentValues(Recipe r, int id) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_COLUMNS[0], id);
        values.put(RECIPE_COLUMNS[1], r.getTitle());
        values.put(RECIPE_COLUMNS[2], r.getAuthor());
        values.put(RECIPE_COLUMNS[3], parser.convertRecipeToString(r));
        return values;
    }
    /**
     * Helper that creates a ContentValues object for the Bunches table
     * @param b Bunch object to take values from
     * @param id int id of bunch
     * @return ContentValues containing the mapping from column name to value for Bunches Table
     */
    private ContentValues createContentValues(Bunch b, int id) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_COLUMNS[0], id);
        values.put(RECIPE_COLUMNS[1], b.getTitle());
        return values;
    }

    /**
     * Helper that creates a list of ContentValues for the Bunch Recipes table
     * @param b Bunch object
     * @param bunch_id int id of bunch
     * @param recipe_ids Map of (recipe name, recipe author) to recipe id
     * @return List of ContentValues containing the mapping from column name to value for the Bunch Recipes table
     */
    private List<ContentValues> createContentValues(Bunch b, int bunch_id, Map<Pair<String, String>, Integer> recipe_ids) {
        List<ContentValues> values_list = new ArrayList<>();
        for (Recipe r: b.getRecipes()) {
            ContentValues values = createContentValues(bunch_id, recipe_ids.get(new Pair<String, String>(r.getTitle(), r.getAuthor())));
            values_list.add(values);
        }
        return values_list;
    }

    /**
     * Helper that creates a ContentValues object for the Bunch Recipes table
     * @param bunch_id int id of bunch
     * @param recipe_id int id of recipe
     * @return ContentValues object containing the mapping from column name to value for the Bunch Recipes Table
     */
    private ContentValues createContentValues(int bunch_id, int recipe_id) {
        ContentValues values = new ContentValues();
        values.put(BUNCH_RECIPE_COLUMNS[0], bunch_id);
        values.put(BUNCH_RECIPE_COLUMNS[1], recipe_id);
        return values;
    }

    /**
     * Private helper class that has methods that allows for access to the underlying android sqlite database
     */
    private class RecipeOpenHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 2;

        public RecipeOpenHelper(Context c) {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(RECIPE_TABLE_CREATE);
            db.execSQL(RECIPE_IMAGE_TABLE_CREATE);
            db.execSQL(BUNCH_TABLE_CREATE);
            db.execSQL(BUNCH_RECIPE_TABLE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //do nothing for now
        }
    }

    /**
     * Returns the row ID of the row inserted by the last INSERT statement
     * @param db the database to access
     * @return the row ID of the most recently inserted row
     */
    private static long lastInsertRowId(SQLiteDatabase db) {
        final Cursor result = db.rawQuery("SELECT last_insert_rowid()", new String[0]);
        try {
            if (!result.moveToFirst()) {
                throw new IllegalStateException("No last insert ID");
            }
            return result.getLong(0);
        }
        finally {
            result.close();
        }
    }
}
