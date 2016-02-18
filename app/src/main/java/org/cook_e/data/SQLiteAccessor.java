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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kylewoo on 2/17/16.
 */
public class SQLiteAccessor {
    private StorageParser parser;
    private RecipeOpenHelper helper;
    private static final String DATABASE_NAME = "RecipesDatabase";
    private static final String RECIPE_TABLE_NAME = "Recipes";
    private static final String[] RECIPE_COLUMNS = {"id", "name", "author", "description"}; // note the indexes are 0 based
    private static final String RECIPE_IMAGE_TABLE_NAME = "RecipeImages";
    private static final String[] RECIPE_IMAGE_COLUMNS = {"id", "image"};
    private static final String BUNCH_TABLE_NAME = "Bunches";
    private static final String[] BUNCH_COLUMNS = {"id", "name"};
    private static final String BUNCH_RECIPES_TABLE_NAME = "Bunch Recipes";
    private static final String[] BUNCH_RECIPE_COLUMNS = {"bunch id", "recipe id"};
    private static final String RECIPE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_TABLE_NAME + " (" +
                    " \"" + RECIPE_COLUMNS[0] + "\" INT NOT NULL DEFAULT 0" +
                    " \"" + RECIPE_COLUMNS[1]  +"\" TEXT NOT NULL DEFAULT \"\"" +
                    " \"" + RECIPE_COLUMNS[2]  + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " \"" + RECIPE_COLUMNS[3]  + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " PRIMARY KEY (" + RECIPE_COLUMNS[0] + "));";

    private static final String RECIPE_IMAGE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_IMAGE_TABLE_NAME + " (" +
            " \"" + RECIPE_IMAGE_COLUMNS[0] + "\" INT NOT NULL DEFAULT 0" +
            " \"" + RECIPE_IMAGE_COLUMNS[1] + "\" BLOB NOT NULL" +
            " PRIMARY KEY (" + RECIPE_IMAGE_COLUMNS[0] + ", " + RECIPE_IMAGE_COLUMNS[1] + "));";

    private static final String BUNCH_TABLE_CREATE =
            "CREATE TABLE " + BUNCH_TABLE_NAME + " (" +
                    " \"" + BUNCH_COLUMNS[0] + "\" INT NOT NULL DEFAULT 0" +
                    " \"" + BUNCH_COLUMNS[1] + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " PRIMARY KEY (" + BUNCH_COLUMNS[0] + "));";
    private static final String BUNCH_RECIPE_TABLE_CREATE =
            "CREATE TABLE " + BUNCH_RECIPES_TABLE_NAME + " (" +
                    " \"" + BUNCH_RECIPE_COLUMNS[0] + "\" INT NOT NULL DEFAULT 0" +
                    " \"" + BUNCH_RECIPE_COLUMNS[1] + "\" INT NOT NULL DEFAULT 0" +
                    " PRIMARY KEY (" + BUNCH_RECIPE_COLUMNS[0] + ", " + BUNCH_RECIPE_COLUMNS[1] + "));";
    public SQLiteAccessor(Context c, StorageParser parser) {
        helper = new RecipeOpenHelper(c);
        this.parser = parser;
    }
    public void storeRecipe(Recipe r, int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = createContentValues(r, id);
        db.insert(RECIPE_TABLE_NAME, null, values);
        db.close();
    }
    public void storeBunch(Bunch b, int bunch_id, Map<Pair<String, String>,Integer> recipe_ids) {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<ContentValues> values_list = createContentValues(b, bunch_id, recipe_ids);
        ContentValues bunch_values = createContentValues(b, bunch_id);
        db.insert(BUNCH_TABLE_NAME, null, bunch_values);
        for (ContentValues values: values_list) {
            db.insert(BUNCH_RECIPES_TABLE_NAME, null, values);
        }
        db.close();

    }
    public void editRecipe(Recipe r, int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = createContentValues(r, id);
        String[] whereArgs = {r.getTitle(), r.getAuthor()};
        db.update(RECIPE_TABLE_NAME, values, "id = ?", whereArgs);
        db.close();
    }
    public void editBunch(Bunch b, int bunch_id, Map<Pair<String, String>, Integer> recipe_ids) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues bunch_values = createContentValues(b, bunch_id);
        String[] bunchArgs = {String.valueOf(bunch_id)};
        db.update(BUNCH_TABLE_NAME, bunch_values, "id = ?", bunchArgs);
        db.delete(BUNCH_RECIPES_TABLE_NAME, "id = ?", bunchArgs);
        List<ContentValues> bunch_recipe_values = createContentValues(b, bunch_id, recipe_ids);
        for (ContentValues cv: bunch_recipe_values) {
            db.insert(BUNCH_RECIPES_TABLE_NAME, null, cv);
        }
        db.close();
    }
    public Recipe loadRecipe(int id) {
        Recipe r = null;
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
        }
        db.close();

        return r;
    }
    public Bunch loadBunch(int id) {
        Bunch b = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] whereArgs = {String.valueOf(id)};
        Cursor c = db.query(BUNCH_TABLE_NAME, BUNCH_COLUMNS, "id = ?", whereArgs,
                null, null, "name");
        List<Recipe> recipes = new ArrayList<Recipe>();
        if (c != null) {
            c.moveToFirst();
            String name = c.getString(1);
            Cursor recipe_bunch_cursor = db.query(BUNCH_RECIPES_TABLE_NAME, BUNCH_RECIPE_COLUMNS,
                    "id = ?", whereArgs,
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
                } while (recipe_bunch_cursor.moveToNext());
                b = new Bunch(name, recipes);
            }
        }
        db.close();
        return b;
    }
    public void deleteRecipe(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {String.valueOf(id)};
        db.delete(RECIPE_TABLE_NAME, "id = ?", whereArgs);
    }
    public void deleteBunch(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {String.valueOf(id)};
        db.delete(BUNCH_TABLE_NAME, "id = ?", whereArgs);
    }
    private ContentValues createContentValues(Recipe r, int id) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_COLUMNS[0], id);
        values.put(RECIPE_COLUMNS[1], r.getTitle());
        values.put(RECIPE_COLUMNS[2], r.getAuthor());
        values.put(RECIPE_COLUMNS[3], parser.convertRecipeToString(r));
        return values;
    }
    private ContentValues createContentValues(Bunch b, int id) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_COLUMNS[0], id);
        values.put(RECIPE_COLUMNS[1], b.getTitle());
        return values;
    }
    private List<ContentValues> createContentValues(Bunch b, int bunch_id, Map<Pair<String, String>, Integer> recipe_ids) {
        List<ContentValues> values_list = new ArrayList<>();
        for (Recipe r: b.getRecipes()) {
            ContentValues values = createContentValues(bunch_id, recipe_ids.get(new Pair<String, String>(r.getTitle(), r.getAuthor())));
            values_list.add(values);
        }
        return values_list;
    }
    private ContentValues createContentValues(int bunch_id, int recipe_id) {
        ContentValues values = new ContentValues();
        values.put(BUNCH_RECIPE_COLUMNS[0], bunch_id);
        values.put(BUNCH_RECIPE_COLUMNS[1], recipe_id);
        return values;
    }
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
}
