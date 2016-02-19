
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
import android.database.sqlite.SQLiteDatabase;

import org.atteo.evo.inflector.English;
import org.cook_e.data.Recipe;
import org.cook_e.data.SQLiteAccessor;
import org.cook_e.data.Step;
import org.cook_e.data.StorageParser;

import java.util.List;
import java.util.ArrayList;
import android.support.test.InstrumentationRegistry;

import dalvik.annotation.TestTargetClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SqliteUnitTest {
    private Context mContext;
    private SQLiteAccessor accessor;
    @Before
    public void setup() {
        StorageParser parser = new StorageParser();
        mContext = InstrumentationRegistry.getTargetContext();
        accessor = new SQLiteAccessor(mContext, parser);

    }
    @After
    public void teardown() {
        accessor = null;
    }
    @Test
    public void testTableInsert() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 0, 5, false);
        int id = 1;
        accessor.storeRecipe(r, 1);
        Recipe result = accessor.loadRecipe(id);
        assertEquals(r, result);

    }
    @Test
    public void testTableDelete() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 0, 5, false);
        int id = 1;
        accessor.storeRecipe(r, 1);
        accessor.deleteRecipe(1);
        Recipe result = accessor.loadRecipe(id);
        assertEquals(null, result);
    }
    @Test
    public void testTableEdit() {
        Recipe r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 0, 5, false);
        Recipe edited_r = RecipeUnitTest.createGenericRecipe("My Recipe", "Kyle Woo", 1, 1, 0, 5, false);
        int id = 1;
        accessor.storeRecipe(r, id);
        Recipe result = accessor.loadRecipe(id);
        assertEquals(r, result);
        accessor.editRecipe(edited_r, id);
        result = accessor.loadRecipe(id);
        assertEquals(edited_r, result);
    }
}