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

import android.util.Log;

import org.atteo.evo.inflector.English;
import org.cook_e.data.Ingredient;
import org.cook_e.data.Step;
import java.util.List;
import java.util.ArrayList;

import org.joda.time.Duration;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.NullPointerException;

import dalvik.annotation.TestTarget;

import static org.junit.Assert.*;
/*
 * Unit Tests for the Step class
 */
public class StepUnitTest {
    /**
     * Helper method that creates a step with 1 ingredient
     * @param action_index index into the ACTION array from the UnitTestSharedData class
     * @param ing_index index into the INGREDIENTS array from the UnitTestSharedData class
     * @param duration_min the number of minutes the duration of the step should be
     */
    public static Step createGenericStep(int action_index, int ing_index, int duration_min, boolean isSimultaneous) {
        List<String> ings = new ArrayList<>();
        String action = UnitTestSharedData.ACTIONS[action_index];
        String ing = UnitTestSharedData.INGREDIENTS[ing_index];
        ings.add(ing);
        return new Step(ings, UnitTestSharedData.generateDescription(ing, action), Duration.standardMinutes(duration_min), isSimultaneous);
    }

    @Test
    public void testCreation() {
        List<String> ings = new ArrayList<>();
        String ing = UnitTestSharedData.INGREDIENTS[0];
        ings.add(ing);
        String action = UnitTestSharedData.ACTIONS[0];

        String description = UnitTestSharedData.generateDescription(ing, action);
        Duration duration = Duration.standardMinutes(1);
        Step s = new Step(ings, description, duration, false);

        assertEquals(ings, s.getIngredients());
        assertEquals(description, s.getDescription());
        assertEquals(duration, s.getTime());
        assertEquals(false, s.isSimultaneous());
    }
    @Test
    public void testEquals() {

        Step s1 = createGenericStep(0, 0, 5, false);
        Step s1_match = createGenericStep(0, 0, 5, false);
        Step s2 = createGenericStep(1, 0, 5, false);
        Step s3 = createGenericStep(0, 1, 5, false);
        Step s4 = createGenericStep(0, 0, 5, true);
        assertEquals(s1, s1_match);
        /*assertFalse(s1.equals(s2));
        assertFalse(s1.equals(s3));
        assertFalse(s1.equals(s4));*/
    }
    @Test
    public void testListToString() {
        List<String> lst = new ArrayList<>();
        lst.add("hello");
        lst.add("goodbye");
        lst.add("test");
        assertEquals("hello,goodbye,test", Step.ListToString(lst));
    }
}
