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

/**
 * Created by kylewoo on 2/17/16.
 */
public class SQLiteAccessor {
    private StorageParser parser;
    private RecipeOpenHelper helper;
    private static final String DATABASE_NAME = "RecipesDatabase";
    private static final String RECIPE_TABLE_NAME = "Recipes";
    private static final String[] RECIPE_COLUMNS = {"name", "author", "description"}; // note the indexes are 0 based
    private static final String RECIPE_IMAGE_TABLE_NAME = "RecipeImages";
    private static final String[] RECIPE_IMAGE_COLUMNS = {"name", "author", "image"};
    private static final String BUNCH_TABLE_NAME = "Bunches";
    private static final String[] BUNCH_COLUMNS = {"bunch name", "recipe name", "author"};
    private static final String RECIPE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_TABLE_NAME + " (" +
                    " \"" + RECIPE_COLUMNS[0]  +"\" TEXT NOT NULL DEFAULT \"\"" +
                    " \"" + RECIPE_COLUMNS[1]  + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " \"" + RECIPE_COLUMNS[2]  + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " PRIMARY KEY (" + RECIPE_COLUMNS[0] +", " + RECIPE_COLUMNS[1] + "));";

    private static final String RECIPE_IMAGE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_IMAGE_TABLE_NAME + " (" +
            " \"" + RECIPE_IMAGE_COLUMNS[0] + "\" TEXT NOT NULL DEFAULT \"\"" +
            " \"" + RECIPE_IMAGE_COLUMNS[1] + "\" TEXT NOT NULL DEFAULT \"\"" +
            " \"" + RECIPE_IMAGE_COLUMNS[2] + "\" BLOB NOT NULL" +
            " PRIMARY KEY (" + RECIPE_IMAGE_COLUMNS[0] + ", " + RECIPE_IMAGE_COLUMNS[1] + ", " +
            RECIPE_IMAGE_COLUMNS[2] + "));";

    private static final String BUNCH_TABLE_CREATE =
            "CREATE TABLE " + BUNCH_TABLE_NAME + " (" +
                    " \"" + BUNCH_COLUMNS[0] + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " \"" + BUNCH_COLUMNS[1] + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " \"" + BUNCH_COLUMNS[2] + "\" TEXT NOT NULL DEFAULT \"\"" +
                    " PRIMARY KEY (" + BUNCH_COLUMNS[0] + ", " + BUNCH_COLUMNS[1] + ", " +
                    BUNCH_COLUMNS[2] + "));";
    public SQLiteAccessor(Context c, StorageParser parser) {
        helper = new RecipeOpenHelper(c);
        this.parser = parser;
    }
    public void storeRecipe(Recipe r) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECIPE_COLUMNS[0], r.getTitle());
        values.put(RECIPE_COLUMNS[1], r.getAuthor());
        values.put(RECIPE_COLUMNS[2], parser.convertRecipeToString(r));
        db.insert(RECIPE_TABLE_NAME, null, values);
        db.close();
    }
    public void storeBunch(Bunch b) {
        //TODO determine what parts of a bunch need to be stored
    }
    public Recipe loadRecipe(String title, String author) {
        Recipe r = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] whereArgs = {title, author};
        Cursor c = db.query(RECIPE_TABLE_NAME, RECIPE_COLUMNS, "name = ? AND author = ?", whereArgs,
                null,
                null, "name");
        if (c != null) {
            c.moveToFirst();
            String description = c.getString(2);
            r = parser.convertStringToRecipe(title, author, description);
        }
        db.close();

        return r;
    }
    public Bunch loadBunch(String name) {
        Bunch b = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] whereArgs = {name};

        return b;
    }
    public void deleteRecipe(Recipe r) {
        deleteRecipe(r.getTitle(), r.getAuthor());
    }
    public void deleteRecipe(String title, String author) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {title, author};
        db.delete(RECIPE_TABLE_NAME, "name = ? AND author = ?", whereArgs);
    }
    public void deleteBunch(Bunch b) {

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
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //do nothing for now
        }
    }
}
