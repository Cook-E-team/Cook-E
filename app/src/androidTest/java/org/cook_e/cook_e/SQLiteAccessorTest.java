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
import org.cook_e.data.Recipe;
import org.cook_e.data.SQLiteAccessor;
import org.cook_e.data.Step;
import org.cook_e.data.StorageParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link SQLiteAccessor} class
 */
public class SQLiteAccessorTest {

    private SQLiteAccessor mAccessor;

    @Before
    public void setUp() throws SQLException {
        StorageParser parser = new StorageParser();
        final Context context = InstrumentationRegistry.getTargetContext();
        mAccessor = new SQLiteAccessor(context, parser);
        mAccessor.clearAllTables();
    }

    @After
    public void tearDown() throws SQLException {
        mAccessor.clearAllTables();
        mAccessor = null;
    }

    @Test
    public void testTableInsert() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        Recipe result = mAccessor.loadRecipe("My Recipe", "Kyle Woo");
        assertEquals(r, result);
        mAccessor.checkInvariants();
    }

    @Test
    public void testTableDelete() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        mAccessor.deleteRecipe(r);
        mAccessor.checkInvariants();
        Recipe result = mAccessor.loadRecipe("My Recipe", "Kyle Woo");
        assertNull(result);
        mAccessor.checkInvariants();
    }

    @Test
    public void testTableEdit() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe edited_r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 1, 1, 5,
                false);
        Step edited_s = StepUnitTest.createGenericStep(1, 1, 5, false);
        List<Step> steps = new ArrayList<>();
        steps.add(edited_s);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        Recipe result = mAccessor.loadRecipe("My Recipe", "Kyle Woo");
        mAccessor.checkInvariants();
        assertEquals(r, result);
        r.setSteps(steps);

        mAccessor.editRecipe(r);
        mAccessor.checkInvariants();
        result = mAccessor.loadRecipe("My Recipe", "Kyle Woo");
        mAccessor.checkInvariants();
        assertEquals(edited_r, result);
    }

    @Test
    public void testBunchInsert() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.add(r);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        Bunch b = new Bunch("My Bunch", recipes);
        mAccessor.storeBunch(b);
        mAccessor.checkInvariants();
        Bunch result = mAccessor.loadBunch("My Bunch");
        mAccessor.checkInvariants();
        assertEquals(b, result);
    }

    @Test
    public void testBunchDelete() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.add(r);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        Bunch b = new Bunch("My Bunch", recipes);
        mAccessor.storeBunch(b);
        mAccessor.checkInvariants();
        mAccessor.deleteBunch(b);
        mAccessor.checkInvariants();
        Bunch result = mAccessor.loadBunch("My Bunch");
        mAccessor.checkInvariants();
        assertNull(result);
    }

    @Test
    public void testBunchEdit() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe r2 = RecipeUnitTest.createGenericRecipe("My Recipe 2", "Kyle Woo", 0, 0, 5, false);
        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.add(r);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        mAccessor.storeRecipe(r2);
        mAccessor.checkInvariants();
        Bunch b = new Bunch("My Bunch", recipes);
        mAccessor.storeBunch(b);
        mAccessor.checkInvariants();
        Bunch result = mAccessor.loadBunch("My Bunch");
        mAccessor.checkInvariants();
        assertEquals(b, result);
        recipes.clear();
        recipes.add(r2);
        b.setRecipes(recipes);
        mAccessor.editBunch(b);
        mAccessor.checkInvariants();
        result = mAccessor.loadBunch("My Bunch");
        mAccessor.checkInvariants();
        assertEquals(b, result);
    }

    @Test
    public void testLoadAllRecipes() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe r2 = RecipeUnitTest.createGenericRecipe("My Recipe 2", "Kyle Woo", 0, 0, 5, false);
        List<Recipe> expected = new ArrayList<>();
        expected.add(r);
        expected.add(r2);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        mAccessor.storeRecipe(r2);
        mAccessor.checkInvariants();
        List<Recipe> result = mAccessor.loadAllRecipes();
        mAccessor.checkInvariants();
        assertEquals(expected, result);
    }

    @Test
    public void testLoadAllBunches() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe r2 = RecipeUnitTest.createGenericRecipe("My Recipe 2", "Kyle Woo", 0, 0, 5, false);
        Recipe r3 = RecipeUnitTest.createGenericRecipe("My Recipe 3", "Kyle Woo", 0, 0, 5, false);
        List<Recipe> b1_list = new ArrayList<>();
        List<Recipe> b2_list = new ArrayList<>();
        b1_list.add(r);
        b1_list.add(r2);
        b2_list.add(r);
        b2_list.add(r3);
        Bunch b1 = new Bunch("Bunch 1", b1_list);
        Bunch b2 = new Bunch("Bunch 2", b2_list);
        List<Bunch> expected = new ArrayList<>();
        expected.add(b1);
        expected.add(b2);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        mAccessor.storeRecipe(r2);
        mAccessor.checkInvariants();
        mAccessor.storeRecipe(r3);
        mAccessor.checkInvariants();
        mAccessor.storeBunch(b1);
        mAccessor.checkInvariants();
        mAccessor.storeBunch(b2);
        mAccessor.checkInvariants();
        List<Bunch> result = mAccessor.loadAllBunches();
        mAccessor.checkInvariants();
        assertEquals(expected, result);
    }
}
