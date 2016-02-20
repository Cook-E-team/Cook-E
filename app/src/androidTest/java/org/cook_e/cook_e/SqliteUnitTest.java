
/* Copyright 2016 the Cook-E development team
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

package org.cook_e.cook_e;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.cook_e.data.Bunch;
import org.cook_e.data.Pair;
import org.cook_e.data.Recipe;
import org.cook_e.data.SQLiteAccessor;
import org.cook_e.data.StorageParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SqliteUnitTest {
    private Context mContext;
    private SQLiteAccessor accessor;
    private Map<Pair<String, String>, Integer> recipe_ids;
    @Before
    public void setup() {
        StorageParser parser = new StorageParser();
        mContext = InstrumentationRegistry.getTargetContext();
        accessor = new SQLiteAccessor(mContext, parser);
        recipe_ids = new HashMap<Pair<String, String>, Integer>();

    }
    @After
    public void teardown() {
        accessor = null;
        recipe_ids = null;
    }
    @Test
    public void testTableInsert() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        try {
        accessor.storeRecipe(r);
        Recipe result = accessor.loadRecipe("My Recipe", "Kyle Woo");
            assertEquals(r, result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testTableDelete() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        int id = 1;
        try {
            accessor.storeRecipe(r);
            accessor.deleteRecipe(r);
            Recipe result = accessor.loadRecipe("My Recipe", "Kyle Woo");
            assertNull(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testTableEdit() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe edited_r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 1, 1, 5, false);
        try {
        accessor.storeRecipe(r);
        Recipe result = accessor.loadRecipe("My Recipe", "Kyle Woo");
        assertEquals(r, result);
        accessor.editRecipe(edited_r);
        result = accessor.loadRecipe("My Recipe", "Kyle Woo");
        assertEquals(edited_r, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testBunchInsert() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        recipes.add(r);
        try {
        accessor.storeRecipe(r);
        Bunch b = new Bunch("My Bunch", recipes);
        accessor.storeBunch(b);
        Bunch result = accessor.loadBunch("My Bunch");
        assertEquals(b, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testBunchDelete() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        recipes.add(r);
        try {
        accessor.storeRecipe(r);
        Bunch b = new Bunch("My Bunch", recipes);
        accessor.storeBunch(b);
        accessor.deleteBunch(b);
        Bunch result = accessor.loadBunch("My Bunch");
        assertNull(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testBunchEdit() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe r2 = RecipeUnitTest.createGenericRecipe("My Recipe 2", "Kyle Woo", 0, 0, 5, false);
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        recipes.add(r);
        try {
        accessor.storeRecipe(r);
        accessor.storeRecipe(r2);
        Bunch b = new Bunch("My Bunch", recipes);
        accessor.storeBunch(b);
        Bunch result = accessor.loadBunch("My Bunch");
        assertEquals(b, result);
        recipes.clear();
        recipes.add(r2);
        b.setRecipes(recipes);
        accessor.editBunch(b);
        result = accessor.loadBunch("My Bunch");
        assertEquals(b, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
