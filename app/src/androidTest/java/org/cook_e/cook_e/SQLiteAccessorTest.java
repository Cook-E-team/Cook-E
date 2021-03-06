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
import java.util.Arrays;
import java.util.Collections;
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
    public void testRecipeInsert() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        Recipe result = mAccessor.loadRecipe("My Recipe", "Kyle Woo");
        assertEquals(r, result);
        mAccessor.checkInvariants();
    }

    @Test
    public void testRecipeDelete() throws SQLException {
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
    public void testRecipeEdit() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe edited_r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 1, 1, 5,
                false);
        Step edited_s = StepUnitTest.createGenericStep(1, 1, 5, false, 0);
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

    /**
     * Tests creating a bunch containing a recipe, deleting the recipe, and querying for the bunch
     * again
     * @throws SQLException
     */
    @Test
    public void testDeleteRecipeInBunch() throws SQLException {
        final String mealName = "The 42905204th meal";
        final Recipe recipe = new Recipe("A Recipe", "Alan Smithee", Collections.<Step>emptyList());

        mAccessor.storeRecipe(recipe);

        final Bunch meal = new Bunch(mealName, Collections.singletonList(recipe));

        mAccessor.storeBunch(meal);
        mAccessor.checkInvariants();

        mAccessor.deleteRecipe(recipe);
        mAccessor.checkInvariants();

        // Remove recipe from bunch, retrieve and check equality
        meal.setRecipes(Collections.<Recipe>emptyList());

        final Bunch retrievedMeal = mAccessor.loadBunch(mealName);
        assertEquals(meal, retrievedMeal);
    }

    public void testLoadBunchByName(String name) throws SQLException {
        final Bunch meal = new Bunch(name, Collections.<Recipe>emptyList());
        mAccessor.storeBunch(meal);
        mAccessor.checkInvariants();

        final Bunch retrieved = mAccessor.loadBunch(name);
        assertEquals(meal, retrieved);
    }

    @Test
    public void testLoadBunchByNameBasic() throws SQLException {
        testLoadBunchByName("Feast 32");
    }

    @Test
    public void testLoadBunchByNameUnicode() throws SQLException {
        testLoadBunchByName("شد. او در کودکی به دیدن فیلم\u200Cهای ترسناک و علمی–تخیلی علاقه داشت و ");
    }

    public void testLoadRecipeByName(String title, String author) throws SQLException {
        final Recipe recipe = new Recipe(title, author, Collections.<Step>emptyList());
        mAccessor.storeRecipe(recipe);
        mAccessor.checkInvariants();
        final Recipe retrieved = mAccessor.loadRecipe(title, author);
        assertEquals(recipe, retrieved);
    }

    @Test
    public void testLoadRecipeByNameBasic() throws SQLException {
        testLoadRecipeByName("Cheese Souffle", "Alan Smithee");
    }
    @Test
    public void testLoadRecipeByNameUnicode() throws SQLException {
        testLoadRecipeByName("Bánh chuối", "Bánh chuối là một loại bánh phổ biến tại Việt Nam");
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
        List<Recipe> result = mAccessor.loadAllRecipes(2);
        mAccessor.checkInvariants();
        assertEquals(expected, result);
    }

    @Test
    public void testLoadAllBunches() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        Recipe r2 = RecipeUnitTest.createGenericRecipe("My Recipe 2", "Kyle Woo", 0, 0, 5, false);
        Recipe r3 = RecipeUnitTest.createGenericRecipe("My Recipe 3", "Kyle Woo", 0, 0, 5, false);

        mAccessor.storeRecipe(r);
        mAccessor.checkInvariants();
        mAccessor.storeRecipe(r2);
        mAccessor.checkInvariants();
        mAccessor.storeRecipe(r3);
        mAccessor.checkInvariants();

        // Recipes must be added to bunches after they are stored so that their IDs will be set
        // correctly
        final Bunch b1 = new Bunch("Bunch 1", Arrays.asList(r, r2));
        final Bunch b2 = new Bunch("Bunch 2", Arrays.asList(r, r3));
        final List<Bunch> expected = Arrays.asList(b1, b2);

        mAccessor.storeBunch(b1);
        mAccessor.checkInvariants();
        mAccessor.storeBunch(b2);
        mAccessor.checkInvariants();
        List<Bunch> result = mAccessor.loadAllBunches(2);
        mAccessor.checkInvariants();
        assertEquals(expected, result);
    }
}
