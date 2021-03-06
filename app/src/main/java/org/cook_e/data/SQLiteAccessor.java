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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private static final String[] RECIPE_COLUMNS = {"id", "name", "author", "description", "image_path"}; // note the indexes are 0 based
    private static final String BUNCH_TABLE_NAME = "Bunches";
    private static final String[] BUNCH_COLUMNS = {"id", "name"};
    private static final String BUNCH_RECIPES_TABLE_NAME = "BunchRecipes";
    private static final String[] BUNCH_RECIPE_COLUMNS = {"bunch_id", "recipe_id"};
    private static final String LEARNER_TABLE_NAME = "LearnerData";
    private static final String[] LEARNER_COLUMNS = {"recipe_id", "step_index", "weighted_time", "learn_rate"};
    /**
     * Schema of the Recipes table: (id, name, author, description)
     */
    private static final String RECIPE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_TABLE_NAME + " (" +
                    RECIPE_COLUMNS[0] + " INTEGER PRIMARY KEY," +
                    RECIPE_COLUMNS[1] + " TEXT NOT NULL DEFAULT \"\"," +
                    RECIPE_COLUMNS[2] + " TEXT NOT NULL DEFAULT \"\"," +
                    RECIPE_COLUMNS[3] + " TEXT NOT NULL DEFAULT \"\", " +
                    RECIPE_COLUMNS[4] + " TEXT DEFAULT NULL);";

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
    private static final String LEARNER_TABLE_CREATE =
            "CREATE TABLE " + LEARNER_TABLE_NAME + " (" +
                    LEARNER_COLUMNS[0] + " INT NOT NULL DEFAULT 0, " +
                    LEARNER_COLUMNS[1] + " INT NOT NULL DEFAULT 0, " +
                    LEARNER_COLUMNS[2] + " REAL NOT NULL DEFAULT 0.0, " +
                    LEARNER_COLUMNS[3] + " REAL NOT NULL DEFAULT 0.0," +
                    " PRIMARY KEY (" + LEARNER_COLUMNS[0] + ", " + LEARNER_COLUMNS[1] +
                   "));";

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
                db.insertWithOnConflict(RECIPE_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Store a bunch on the sqlite database
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

                db.delete(LEARNER_TABLE_NAME, "recipe_id=?", whereArgs);
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
                for (Recipe r: b.getRecipes()) {
                    ContentValues cv = createContentValues(b,r);
                    db.insert(BUNCH_RECIPES_TABLE_NAME, null, cv);
                    String[] whereArgs = {String.valueOf(r.getObjectId())};
                    db.delete(LEARNER_TABLE_NAME, "recipe_id=?", whereArgs);
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
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                String[] whereArgs = {title, author};
                Cursor c = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, "name = ? AND author = ?",
                        whereArgs,
                        null, null, "name");
                try {
                    if (c.moveToFirst()) {
                        return recipeFromResult(c);
                    } else {
                        return null;
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
    }

    /**
     * Creates a recipe from a result
     * @param result a result, which must not be null and must already be pointing to a valid row
     * @return a Recipe
     */
    private Recipe recipeFromResult(Cursor result) throws SQLException, ParseException {
        final long id = result.getLong(result.getColumnIndexOrThrow(RECIPE_COLUMNS[0]));
        final String name = result.getString(result.getColumnIndexOrThrow(RECIPE_COLUMNS[1]));
        final String author = result.getString(result.getColumnIndexOrThrow(RECIPE_COLUMNS[2]));
        final String stepString = result.getString(result.getColumnIndexOrThrow(RECIPE_COLUMNS[3]));
        final String imagePath = result.getString(result.getColumnIndexOrThrow(RECIPE_COLUMNS[4]));

        final Recipe recipe = new Recipe(name, author, mParser.parseRecipeSteps(stepString));
        recipe.setObjectId(id);
        recipe.setImageLink(imagePath);
        return recipe;
    }

    /**
     * Load all recipes off the database
     *
     * @return List of Recipes
     */
    @Override
    public List<Recipe> loadAllRecipes(int limit) throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                Cursor c = null;
                if (limit == -1) {
                    c = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, null, null, null, null,
                            "name");
                } else {
                    c  = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, null, null, null, null,
                            "name", String.valueOf(limit));
                }
                try {
                    while (c.moveToNext()) {
                        recipes.add(recipeFromResult(c));
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
     * @param title a recipe description
     * @return does not return
     * @throws SQLException
     */
    @Override
    public List<Recipe> findRecipesLike(String title) throws SQLException {
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
    public List<Bunch> loadAllBunches(int limit) throws SQLException {
        List<Bunch> bunches = new ArrayList<>();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                Cursor c = null;
                if (limit == -1) {
                    c = db.query(BUNCH_TABLE_NAME, BUNCH_COLUMNS, null, null, null, null,
                            "name");
                } else {
                    c = db.query(BUNCH_TABLE_NAME, BUNCH_COLUMNS, null, null, null, null,
                            "name", String.valueOf(limit));
                }
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
                                    Recipe r = loadRecipe(recipe_id, db);
                                    recipes.add(r);
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
                        final String bunchName = c.getString(1);
                        // Create the bunch
                        b = new Bunch(bunchName, Collections.<Recipe>emptyList());

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
                                    Recipe r = loadRecipe(recipe_id, db);
                                    recipes.add(r);
                                } while (recipe_bunch_cursor.moveToNext());
                                b = new Bunch(name, recipes);
                                b.setObjectId(bunch_id);
                            }
                        } finally {
                            recipe_bunch_cursor.close();
                        }
                        b.setRecipes(recipes);
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
            db.beginTransaction();
            try {
                String[] whereArgs = {String.valueOf(r.getObjectId())};
                // Remove any bunch associations that involve this recipe
                db.delete(BUNCH_RECIPES_TABLE_NAME, "recipe_id = ?", whereArgs);

                // Delete the recipe entry
                db.delete(RECIPE_TABLE_NAME, "id = ?", whereArgs);
                db.delete(LEARNER_TABLE_NAME, "recipe_id = ?", whereArgs);
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
    @Override
    public void storeLearnerData(Recipe r, Collection<LearningWeight> weights) throws SQLException{
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                List<ContentValues> learner_cvs = createContentValues(r, weights);
                for (ContentValues cv : learner_cvs) {
                    db.insert(LEARNER_TABLE_NAME, null, cv);
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
    @Override
    public void updateLearnerData(Recipe r, LearningWeight lw) throws SQLException {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        try {
            ContentValues cv = createContentValues(r, lw);
            String[] whereArgs = {String.valueOf(lw)};
            db.insertWithOnConflict(LEARNER_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            db.close();
        }
    }

    @Override
    public List<LearningWeight> loadLearnerData(Recipe r) throws SQLException {
        List<LearningWeight> results = new ArrayList<>();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            try {
                String[] whereArgs = {String.valueOf(r.getObjectId())};
                Cursor c = db.query(LEARNER_TABLE_NAME, LEARNER_COLUMNS, "recipe_id = ?", whereArgs, null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        int index = c.getInt(1);
                        double weighted_time = c.getDouble(2);
                        double learn_rate = c.getDouble(3);

                        LearningWeight weight = new LearningWeight(index, weighted_time, learn_rate);
                        results.add(weight);
                    } while (c.moveToNext());
                    c.close();
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }

        return results;
    }
    public void deleteLearnerData() throws SQLException {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.delete(LEARNER_TABLE_NAME, null, null);
        } finally {
            db.close();
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
        values.put(RECIPE_COLUMNS[4], r.getImageLink());
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
    private List<ContentValues> createContentValues(Recipe r, Collection<LearningWeight> weights) {
        List<ContentValues> values = new ArrayList<>();
        for (LearningWeight weight: weights) {
            values.add(createContentValues(r, weight));
        }
        return values;
    }
    private ContentValues createContentValues(Recipe r, LearningWeight weight) {
        ContentValues values = new ContentValues();
        values.put(LEARNER_COLUMNS[0], r.getObjectId());
        values.put(LEARNER_COLUMNS[1], weight.getIndex());
        values.put(LEARNER_COLUMNS[2], weight.getTimeWeight());
        values.put(LEARNER_COLUMNS[3], weight.getLearnRate());
        return values;
    }

    private Recipe loadRecipe(long recipe_id, SQLiteDatabase db) throws ParseException, SQLException{
        String[] recipeWhereArgs = {String.valueOf(recipe_id)};
        Cursor recipe_cursor = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, "id = ?", recipeWhereArgs,
                null, null, null);
        try {
            if (recipe_cursor.moveToFirst()) {
                return recipeFromResult(recipe_cursor);
            } else {
                throw new SQLException(
                        "No recipe with ID " + recipe_id +
                                " in recipes table");
            }
        } finally {
            recipe_cursor.close();
        }
    }

    @Override
    public void close() throws IOException {
        mHelper.close();
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
            db.execSQL(BUNCH_TABLE_CREATE);
            db.execSQL(BUNCH_RECIPE_TABLE_CREATE);
            db.execSQL(LEARNER_TABLE_CREATE);
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
    @Override
    public void clearAllTables() throws SQLException {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(RECIPE_TABLE_NAME, null, null);
                db.delete(BUNCH_TABLE_NAME, null, null);
                db.delete(BUNCH_RECIPES_TABLE_NAME, null, null);
                db.delete(LEARNER_TABLE_NAME, null, null);
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

    @Override
    public boolean containsRecipe(long id) throws SQLException {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        try {
            // Query for up to 1 row with the matching ID
            final Cursor result = db.query(true, RECIPE_TABLE_NAME,
                    new String[]{ RECIPE_COLUMNS[0] }, "id = ?", new String[]{ Long.toString(id) },
                    null, null, null, "1");
            try {
                return result.getCount() != 0;
            } finally {
                result.close();
            }
        } finally {
            db.close();
        }
    }
}
