
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

import org.cook_e.data.Recipe;
import org.cook_e.data.SQLiteAccessor;
import org.cook_e.data.Step;
import org.cook_e.data.StorageParser;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
        Recipe r = createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 0, 5, false);
        int id = 1;
        accessor.storeRecipe(r, 1);
        Recipe result = accessor.loadRecipe(id);
        assertEquals(r, result);

    }
    @Test
    public void testTableDelete() {
        Recipe r = createGenericRecipe("My Recipe", "Kyle Woo", 0, 0, 0, 5, false);
        int id = 1;
        accessor.storeRecipe(r, 1);
        accessor.deleteRecipe(1);
        Recipe result = accessor.loadRecipe(id);
        assertEquals(null, result);
    }

    /**
     * Creates generic recipe with 1 step
     *
     */
    public static Recipe createGenericRecipe(String name, String author, int action_index, int ing_index, int unit_index, int duration_min, boolean isSimultaneous) {
        Step s = createGenericStep(action_index, ing_index, unit_index, duration_min,
                isSimultaneous);
        List<Step> steps = new ArrayList<Step>();
        steps.add(s);
        return new Recipe(name, author, steps);
    }
    /**
     * Helper method that creates a step with 1 ingredient
     * @param action_index index into the ACTION array from the UnitTestSharedData class
     * @param ing_index index into the INGREDIENTS array from the UnitTestSharedData class
     * @param unit_index index into the  COMMON_UNITS array from the UnitTestSharedData class
     * @param duration_min the number of minutes the duration of the step should be
     */
    public static Step createGenericStep(int action_index, int ing_index, int unit_index, int duration_min, boolean isSimultaneous) {
        List<String> ings = new ArrayList<>();
        String action = UnitTestSharedData.ACTIONS[action_index];
        String ing = UnitTestSharedData.INGREDIENTS[ing_index];
        ings.add(ing);
        return new Step(ings,UnitTestSharedData.generateDescription(ing, action), Duration.standardMinutes(
                duration_min), isSimultaneous);
    }
}
