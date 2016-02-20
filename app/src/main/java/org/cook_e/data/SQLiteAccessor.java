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

/**
 * This class implements methods allowing for storage and access to an android local sqlite database
 */
public class SQLiteAccessor implements SQLAccessor {
    /**
     * Parser for transforming a string description into a Recipe and vice versa
     */
    private StorageParser mParser;
    /**
     * Helper that has methods for accessing the android local sqlite database
     */
    private RecipeOpenHelper mHelper;
    /**
     * Counter used to set ids for recipes
     */
    private long mRecipeCounter;
    /**
     * Counter used to set ids for bunches
     */
    private long mBunchCounter;
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
                    RECIPE_COLUMNS[1] + " TEXT NOT NULL DEFAULT \"\"," +
                    RECIPE_COLUMNS[2] + " TEXT NOT NULL DEFAULT \"\"," +
                    RECIPE_COLUMNS[3] + " TEXT NOT NULL DEFAULT \"\");";
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
     *
     * @param c      Context of the activity that will be using the sqlite database
     * @param parser Parser that will implement String > Recipe and Recipe > String transformation
     */
    public SQLiteAccessor(Context c, StorageParser parser) {
        mHelper = new RecipeOpenHelper(c);
        this.mParser = parser;
        setupCounters();
    }

    /**
     * Store a recipe on the sqlite database
     *
     * @param r Recipe object to store
     */
    @Override
    public void storeRecipe(Recipe r) throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            try {
                if (!r.hasObjectId()) {
                    r.setObjectId(mRecipeCounter++);
                }
                ContentValues values = createContentValues(r);
                db.insert(RECIPE_TABLE_NAME, null, values);
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Store a bunch on the sqlite database
     * Assumes that all recipes within this bunch have been stored on the database
     *
     * @param b Bunch object to store
     */
    @Override
    public void storeBunch(Bunch b) throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                if (!b.hasObjectId()) {
                    b.setObjectId(mBunchCounter++);
                }
                List<ContentValues> values_list = createContentValuesList(b);
                ContentValues bunch_values = createContentValues(b);
                db.insert(BUNCH_TABLE_NAME, null, bunch_values);
                for (ContentValues values : values_list) {
                    db.insert(BUNCH_RECIPES_TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Edit a recipe stored on the sqlite database
     *
     * @param r Recipe object to update
     */
    @Override
    public void editRecipe(Recipe r) throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            try {
                ContentValues values = createContentValues(r);
                String[] whereArgs = {String.valueOf(r.getObjectId())};
                db.update(RECIPE_TABLE_NAME, values, "id = ?", whereArgs);
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Edit a bunch stored on the sqlite database
     *
     * @param b Bunch object to edit
     */
    @Override
    public void editBunch(Bunch b) throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues bunch_values = createContentValues(b);
                String[] bunchArgs = {String.valueOf(b.getObjectId())};
                db.update(BUNCH_TABLE_NAME, bunch_values, "id = ?", bunchArgs);
                db.delete(BUNCH_RECIPES_TABLE_NAME, "bunch_id = ?", bunchArgs);
                List<ContentValues> bunch_recipe_values = createContentValuesList(b);
                for (ContentValues cv : bunch_recipe_values) {
                    db.insert(BUNCH_RECIPES_TABLE_NAME, null, cv);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Load a recipe off the database that matches the provided title and author.
     *
     * If two or more recipes have the same title and author, one will be chosen in an unspecified
     * way and returned.
     *
     * @param title  String title of recipe to load
     * @param author String author of recipe to load
     * @return Recipe object, or null if no matching recipe was found
     */
    @Override
    public Recipe loadRecipe(String title, String author) throws SQLException {
        Recipe r = null;

        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                String[] whereArgs = {title, author};
                Cursor c = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, "name = ? AND author = ?",
                        whereArgs,
                        null, null, "name");
                try {
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        String description = c.getString(3);
                        final List<Step> steps = mParser.parseRecipeSteps(description);
                        r = new Recipe(title, author, steps);
                        r.setObjectId(c.getLong(0));
                        c.close();
                    }
                } finally {
                    c.close();
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }

        return r;
    }

    /**
     * Load all recipes off the database
     *
     * @return List of Recipes
     */
    @Override
    public List<Recipe> loadAllRecipes() throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                Cursor c = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, null, null, null, null,
                        "name");
                try {
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        do {
                            String title = c.getString(1);
                            String author = c.getString(2);
                            String description = c.getString(3);
                            final List<Step> steps = mParser.parseRecipeSteps(description);
                            final Recipe r = new Recipe(title, author, steps);
                            r.setObjectId(c.getLong(0));
                            recipes.add(r);
                        } while (c.moveToNext());
                    }
                } finally {
                    c.close();
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return recipes;
    }

    /**
     * Throws an exception. This method is not implemented in this class
     * @param description a recipe description
     * @return does not return
     * @throws SQLException
     */
    @Override
    public List<Recipe> findRecipesLike(String description) throws SQLException {
        throw new SQLException("Not implemented");
    }

    @Override
    public void checkInvariants() throws SQLException {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        try {
            checkRecipeIds(db);
            checkBunchIds(db);
            checkRecipeBunchTable(db);
        }
        finally {
            db.close();
        }
    }

    /**
     * Checks that no recipe has and ID of {@link DatabaseObject#NO_ID}
     * @param db the database to check
     * @throws SQLException if a recipe has an invalid ID
     */
    private void checkRecipeIds(SQLiteDatabase db) throws SQLException {
        // Query for recipes with id = -1
        final Cursor result = db.query(RECIPE_TABLE_NAME, new String[]{RECIPE_COLUMNS[0]},
                "id = ?", new String[]{Long.toString(DatabaseObject.NO_ID)}, null, null, null, null);
        try {
            if (result.getCount() > 0) {
                throw new SQLException("Invalid database state: Found a recipe with ID -1");
            }
        } finally {
            result.close();
        }
    }
    /**
     * Checks that no bunch has and ID of {@link DatabaseObject#NO_ID}
     * @param db the database to check
     * @throws SQLException if a bunch has an invalid ID
     */
    private void checkBunchIds(SQLiteDatabase db) throws SQLException {
        // Query for bunches with id = -1
        final Cursor result = db.query(BUNCH_TABLE_NAME, new String[]{BUNCH_COLUMNS[0]},
                "id = ?", new String[]{Long.toString(DatabaseObject.NO_ID)}, null, null, null, null);
        try {
            if (result.getCount() > 0) {
                throw new SQLException("Invalid database state: Found a bunch with ID -1");
            }
        } finally {
            result.close();
        }
    }

    /**
     * Checks the recipe-bunch relation table
     * @param db the database to check
     * @throws SQLException if part of the table is invalid
     */
    private void checkRecipeBunchTable(SQLiteDatabase db) throws SQLException {
        // Query for all bunch-recipe relations
        final Cursor result = db.query(BUNCH_RECIPES_TABLE_NAME, BUNCH_RECIPE_COLUMNS, null, null,
                null, null, null, null);
        try {
            while (result.moveToNext()) {
                final long bunchId = result.getLong(0);
                if (bunchId == DatabaseObject.NO_ID) {
                    throw new SQLException("Invalid database state: Bunch ID of -1 in bunch-recipe table");
                }
                final long recipeId = result.getLong(1);
                if (recipeId == DatabaseObject.NO_ID) {
                    throw new SQLException("Invalid database state: Recipe ID of -1 in bunch-recipe table");
                }
                checkBunchExists(db, bunchId);
                checkRecipeExists(db, recipeId);
            }
        }
        finally {
            result.close();
        }
    }

    /**
     * Checks that a recipe with the provided ID exists
     * @param db the database to check
     * @param id the recipe ID to find
     * @throws SQLException if the recipe does not exist
     */
    private void checkRecipeExists(SQLiteDatabase db, long id) throws SQLException {
        final Cursor result = db.query(RECIPE_TABLE_NAME, new String[0], "id = ?",
                new String[]{ Long.toString(id) }, null, null, null);
        try {
            if (result.getCount() != 1) {
                throw new SQLException("Invalid database state: Recipe with id " + id + " does not exist");
            }
        }
        finally {
            result.close();
        }
    }

    /**
     * Checks that a bunch with the provided ID exists
     * @param db the database to check
     * @param id the bunch ID to find
     * @throws SQLException if the bunch does not exist
     */
    private void checkBunchExists(SQLiteDatabase db, long id) throws SQLException {
        final Cursor result = db.query(BUNCH_TABLE_NAME, new String[0], "id = ?",
                new String[]{ Long.toString(id) }, null, null, null);
        try {
            if (result.getCount() != 1) {
                throw new SQLException("Invalid database state: Bunch with id " + id + " does not exist");
            }
        }
        finally {
            result.close();
        }
    }

    /**
     * Load all bunches off the database
     *
     * @return List of bunches
     */
    @Override
    public List<Bunch> loadAllBunches() throws SQLException {
        List<Bunch> bunches = new ArrayList<>();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                Cursor c = db.query(BUNCH_TABLE_NAME, BUNCH_COLUMNS, null, null, null, null,
                        "name");
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        List<Recipe> recipes = new ArrayList<>();
                        long bunch_id = c.getLong(0);
                        String name = c.getString(1);
                        String[] whereArgs = {String.valueOf(bunch_id)};
                        Cursor recipe_bunch_cursor = db.query(BUNCH_RECIPES_TABLE_NAME,
                                BUNCH_RECIPE_COLUMNS, "bunch_id = ?", whereArgs,
                                null, null, null);
                        try {
                            if (recipe_bunch_cursor.getCount() > 0) {
                                recipe_bunch_cursor.moveToFirst();
                                do {
                                    long recipe_id = recipe_bunch_cursor.getLong(
                                            recipe_bunch_cursor.getColumnIndexOrThrow(
                                                    BUNCH_RECIPE_COLUMNS[1]));
                                    String[] recipeWhereArgs = {String.valueOf(recipe_id)};
                                    Cursor recipe_cursor = db.query(RECIPE_TABLE_NAME,
                                            RECIPE_COLUMNS,
                                            "id = ?", recipeWhereArgs,
                                            null, null, null);
                                    try {
                                        if (recipe_cursor.getCount() > 0) {
                                            recipe_cursor.moveToFirst();
                                            String title = recipe_cursor.getString(1);
                                            String author = recipe_cursor.getString(2);
                                            String description = recipe_cursor.getString(3);
                                            final List<Step> steps = mParser.parseRecipeSteps(
                                                    description);
                                            final Recipe r = new Recipe(title, author, steps);
                                            r.setObjectId(recipe_id);
                                            recipes.add(r);
                                        } else {
                                            throw new SQLException(
                                                    "No recipe with ID " + recipe_id +
                                                            " in recipes table");
                                        }
                                    } finally {
                                        recipe_cursor.close();
                                    }
                                } while (recipe_bunch_cursor.moveToNext());
                            }
                            Bunch b = new Bunch(name, recipes);
                            b.setObjectId(bunch_id);
                            bunches.add(b);
                        } finally {
                            recipe_bunch_cursor.close();
                        }
                    } while (c.moveToNext());
                    c.close();
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return bunches;

    }

    /**
     * Load a bunch off the database (and all its contained recipes)
     *
     * @param name String name of Bunch to load
     * @return Bunch object, or null if no matching Bunch could be found
     */
    @Override
    public Bunch loadBunch(String name) throws SQLException {
        Bunch b = null;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                String[] whereArgs = {name};
                Cursor c = db.query(BUNCH_TABLE_NAME, BUNCH_COLUMNS, "name = ?", whereArgs,
                        null, null, "name");
                try {
                    List<Recipe> recipes = new ArrayList<>();
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        long bunch_id = c.getLong(0);
                        String[] bunchRecipesWhereArgs = {String.valueOf(bunch_id)};
                        Cursor recipe_bunch_cursor = db.query(BUNCH_RECIPES_TABLE_NAME,
                                BUNCH_RECIPE_COLUMNS,
                                "bunch_id = ?", bunchRecipesWhereArgs,
                                null, null, null);
                        try {
                            if (recipe_bunch_cursor.getCount() > 0) {
                                recipe_bunch_cursor.moveToFirst();
                                do {
                                    long recipe_id = recipe_bunch_cursor.getLong(1);
                                    String[] recipeWhereArgs = {String.valueOf(recipe_id)};
                                    Cursor recipe_cursor = db.query(RECIPE_TABLE_NAME,
                                            RECIPE_COLUMNS,
                                            "id = ?",
                                            recipeWhereArgs,
                                            null, null, null);
                                    try {
                                        if (recipe_cursor.getCount() > 0) {
                                            recipe_cursor.moveToFirst();
                                            String title = recipe_cursor.getString(1);
                                            String author = recipe_cursor.getString(2);
                                            String description = recipe_cursor.getString(3);
                                            final List<Step> steps = mParser.parseRecipeSteps(
                                                    description);
                                            final Recipe r = new Recipe(title, author, steps);
                                            r.setObjectId(recipe_id);
                                            recipes.add(r);
                                        }
                                    } finally {
                                        recipe_cursor.close();
                                    }
                                } while (recipe_bunch_cursor.moveToNext());
                                b = new Bunch(name, recipes);
                                b.setObjectId(bunch_id);
                            }
                        } finally {
                            recipe_bunch_cursor.close();
                        }
                    }
                } finally {
                    c.close();
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return b;
    }

    /**
     * Delete a recipe from the database
     *
     * @param r Recipe to delete
     */
    @Override
    public void deleteRecipe(Recipe r) throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            try {
                String[] whereArgs = {String.valueOf(r.getObjectId())};
                db.delete(RECIPE_TABLE_NAME, "id = ?", whereArgs);
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Delete a bunch from the database
     *
     * @param b Bunch to delete
     */
    @Override
    public void deleteBunch(Bunch b) throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                String[] whereArgs = {String.valueOf(b.getObjectId())};
                db.delete(BUNCH_TABLE_NAME, "id = ?", whereArgs);
                db.delete(BUNCH_RECIPES_TABLE_NAME, "bunch_id = ?", whereArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Helper that creates a ContentValues object for the Recipes table
     *
     * @param r Recipe object to take values from
     * @return ContentValues containing the mapping from column name to value for Recipes Table
     */
    private ContentValues createContentValues(Recipe r) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_COLUMNS[0], r.getObjectId());
        values.put(RECIPE_COLUMNS[1], r.getTitle());
        values.put(RECIPE_COLUMNS[2], r.getAuthor());
        values.put(RECIPE_COLUMNS[3], mParser.serializeRecipeSteps(r.getSteps()));
        return values;
    }

    /**
     * Helper that creates a ContentValues object for the Bunches table
     *
     * @param b Bunch object to take values from
     * @return ContentValues containing the mapping from column name to value for Bunches Table
     */
    private ContentValues createContentValues(Bunch b) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_COLUMNS[0], b.getObjectId());
        values.put(RECIPE_COLUMNS[1], b.getTitle());
        return values;
    }

    /**
     * Helper that creates a list of ContentValues for the Bunch Recipes table
     *
     * @param b Bunch object
     * @return List of ContentValues containing the mapping from column name to value for the Bunch Recipes table
     */
    private List<ContentValues> createContentValuesList(Bunch b) {
        List<ContentValues> values_list = new ArrayList<>();
        for (Recipe r : b.getRecipes()) {
            ContentValues values = createContentValues(b, r);
            values_list.add(values);
        }
        return values_list;
    }

    /**
     * Helper that creates a ContentValues object for the Bunch Recipes table
     *
     * @param b Bunch object
     * @param r Recipe object in the bunch
     * @return ContentValues object containing the mapping from column name to value for the Bunch Recipes Table
     */
    private ContentValues createContentValues(Bunch b, Recipe r) {
        ContentValues values = new ContentValues();
        values.put(BUNCH_RECIPE_COLUMNS[0], b.getObjectId());
        values.put(BUNCH_RECIPE_COLUMNS[1], r.getObjectId());
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
     * Helper that initiates the counters used to assign ids
     * This is necessary because when a user quits the app this class will be destroyed but the underlying database remains
     * Therefore in order to make sure there are no id collisions we need to check if there are already records in the database
     * and set the counter to the highest id in the database + 1
     * If an error occurs trying to query the database the counters will be set to 0
     */
    private void setupCounters() {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            String[] column = {"id"};
            Cursor recipes = db.query(RECIPE_TABLE_NAME, column, null, null, null, null, "id DESC",
                    "1");
            try {
                if (recipes.getCount() > 0) {
                    recipes.moveToFirst();
                    mRecipeCounter = recipes.getLong(0);
                    mRecipeCounter++;
                } else {
                    mRecipeCounter = 0;
                }
            } finally {
                recipes.close();
            }
            Cursor bunches = db.query(BUNCH_TABLE_NAME, column, null, null, null, null, "id DESC",
                    "1");
            try {
                if (bunches.getCount() > 0) {
                    bunches.moveToFirst();
                    mBunchCounter = bunches.getLong(0);
                    mBunchCounter++;
                } else {
                    mBunchCounter = 0;
                }
            } finally {
                bunches.close();
            }
        } catch (Exception e) {
            mRecipeCounter = 0;
            mBunchCounter = 0;
        }
    }

    /**
     * Warning, this clears all the tables in the database
     * Should only call for testing purposes
     */
    public void clearAllTables() throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(RECIPE_TABLE_NAME, null, null);
                db.delete(BUNCH_TABLE_NAME, null, null);
                db.delete(RECIPE_IMAGE_TABLE_NAME, null, null);
                db.delete(BUNCH_RECIPES_TABLE_NAME, null, null);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
