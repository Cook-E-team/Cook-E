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

package org.cook_e.data;

import org.atteo.evo.inflector.English;
import org.cook_e.data.Ingredient;
import org.cook_e.data.Step;
import org.cook_e.data.UnitTestSharedData;
import java.util.List;
import java.util.ArrayList;

import org.joda.time.Duration;
import org.junit.Test;

import java.lang.NullPointerException;

import static org.junit.Assert.*;

public class StepUnitTest {
    // Create a Step with 1 ingredient
    public static Step createGenericStep(int action_index, int ing_index, int unit_index, int duration_min) {
        List<Ingredient> ings = new ArrayList<Ingredient>();
        String action = UnitTestSharedData.ACTIONS[action_index];
        Ingredient ing = new Ingredient(UnitTestSharedData.INGREDIENTS[ing_index], 1, UnitTestSharedData.COMMON_UNITS[unit_index].word);
        ings.add(ing);
        return new Step(ings, action, UnitTestSharedData.generateDescription(ing.getType(), action), Duration.standardMinutes(duration_min));
    }

    @Test
    public void testCreation() {
        List<Ingredient> ings = new ArrayList<>();
        String action = UnitTestSharedData.ACTIONS[0];
        Ingredient ing = new Ingredient(UnitTestSharedData.INGREDIENTS[0], 1, UnitTestSharedData.COMMON_UNITS[0].word);
        ings.add(ing);
        String description = UnitTestSharedData.generateDescription(ing.getType(), action);
        Duration duration = Duration.standardMinutes(1);
        Step s = new Step(ings, action, description, duration);

        assertEquals(ings, s.getIngredients());
        assertEquals(action, s.getAction());
        assertEquals(description, s.getDescription());
        assertEquals(duration, s.getTime());
    }
    @Test
    public void testEquals() {

        Step s1 = createGenericStep(0, 0, 0, 5);
        Step s1_match = createGenericStep(0, 0, 0, 5);
        Step s2 = createGenericStep(1, 0, 0, 5);
        Step s3 = createGenericStep(0, 1, 0, 5);
        Step s4 = createGenericStep(0, 0, 1, 5);
        Step s5 = createGenericStep(0, 0, 0, 1);


        assertEquals(s1, s1_match);
        assertFalse(s1.equals(s2));
        assertFalse(s1.equals(s3));
        assertFalse(s1.equals(s4));
        assertFalse(s1.equals(s5));
    }

}