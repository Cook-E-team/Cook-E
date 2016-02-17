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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kylewoo on 2/17/16.
 */
public class SQLiteAccessor {
    private Map<Pair<String, String>, Recipe> buffer;
    private StorageParser parser;
    private RecipeOpenHelper helper;
    private static final int BUFFER_LIMIT = 10;
    private static final String DATABASE_NAME = "RecipesDatabase";
    private static final String RECIPE_TABLE_NAME = "Recipes";
    private static final String[] recipe_columns = {"name", "author", "description"}; // note the column number and index are 1 off
    private static final String RECIPE_IMAGE_TABLE_NAME = "RecipeImages";
     public SQLiteAccessor(Context c, StorageParser parser) {
        buffer = new HashMap<Pair<String, String>, Recipe>();
         helper = new RecipeOpenHelper(c);
         this.parser = parser;
    }
    public void storeRecipe(Recipe r) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(recipe_columns[0], r.getTitle());
        values.put(recipe_columns[1], r.getAuthor());
        values.put(recipe_columns[2], parser.convertRecipeToString(r));
        db.insert(RECIPE_TABLE_NAME, null, values);
        db.close();
    }
    public void storeBunch(Bunch b) {
        //TODO determine what parts of a bunch need to be stored
    }
    public Recipe loadRecipe(String title, String author) {
        Recipe r = null;
        r = buffer.get(new Pair<String, String>(title, author));
        if (r == null) {
            SQLiteDatabase db = helper.getReadableDatabase();
            String[] whereArgs = {title, author};
            Cursor c = db.query(RECIPE_TABLE_NAME, recipe_columns, "name = ? AND author = ?", whereArgs,
                    null,
                    null, "name");
            if (c != null) {
                c.moveToFirst();
                String description = c.getString(2);
                r = parser.convertStringToRecipe(title, author, description);
                if (r != null) addToBuffer(r);
            }
            db.close();
        }
        return r;
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
    private void addToBuffer(Recipe r) {
        Set<Pair<String, String>> buffer_keys = buffer.keySet();
        if (buffer_keys.size() > BUFFER_LIMIT) {
            for (Pair<String, String> p: buffer_keys) {
                buffer.remove(p);
                break; //basic way to maintain buffer, remove first element in buffer when it gets to obig
            }
        }
    }
    private class RecipeOpenHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 2;
        private final String RECIPE_TABLE_CREATE =
                "CREATE TABLE " + RECIPE_TABLE_NAME + " (" +
                        " \"" + recipe_columns[0]  +"\" TEXT NOT NULL DEFAULT \"\"" +
                        " \"" + recipe_columns[1]  + "\" TEXT NOT NULL DEFAULT \"\"" +
                        " \"" + recipe_columns[2]  + "\" TEXT NOT NULL DEFAULT \"\"" +
                        "PRIMARY KEY (" + recipe_columns[0] +", " + recipe_columns[1] + "));";
        public RecipeOpenHelper(Context c) {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(RECIPE_TABLE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //do nothing for now
        }
    }
}
