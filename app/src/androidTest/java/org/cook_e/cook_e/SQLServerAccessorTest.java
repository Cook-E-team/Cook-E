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

import org.cook_e.data.Recipe;
import org.cook_e.data.SQLServerAccessor;
import org.cook_e.data.StorageParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.assertEquals;


public class SQLServerAccessorTest {

    private SQLServerAccessor accessor;

    @Before
    public void setup() throws SQLException {
        StorageParser parser = new StorageParser();
        accessor = new SQLServerAccessor(parser);
    }

    @After
    public void teardown() throws SQLException {
        accessor.clearAllTables();
        accessor = null;

    }

    @Test
    public void testRecipeLoad() throws SQLException {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 5, false);
        //accessor.storeRecipe(r);
        Recipe result = accessor.loadRecipe("My Recipe", "Kyle Woo");
        assertEquals(r, result);
    }

    @Test
    public void testRecipeLoadLike() throws SQLException {
        Recipe r1 = RecipeUnitTest.createGenericRecipe("My Recipe 1", "Kyle Woo", 0, 0, 5, false);
        Recipe r2 = RecipeUnitTest.createGenericRecipe("My Recipe 2", "Kyle Woo", 0, 0, 5, false);
        Recipe r3 = RecipeUnitTest.createGenericRecipe("My Recipe 3", "Kyle Woo", 0, 0, 5, false);
        //accessor.storeRecipe(r1);
        //accessor.storeRecipe(r2);
        //accessor.storeRecipe(r3);
        List<Recipe> expected = new ArrayList<>();
        expected.add(r1);
        expected.add(r2);
        expected.add(r3);
        List<Recipe> result = accessor.findRecipesLike("My Recipe");
        assertEquals(expected, result);
    }

}


