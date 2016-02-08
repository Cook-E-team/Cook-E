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
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.NullPointerException;

import static org.junit.Assert.*;

public class RecipeUnitTest {
    @Test
    public void testCreation() {
        List<Step> steps = new ArrayList<Step>();
        Step s = StepUnitTest.createGenericStep(0, 0, 0, 1);
        steps.add(s);
        String author = "Kyle";
        String title = "My Recipe";
        final Recipe r = new Recipe(title, author, steps);
        assertEquals(r.getAuthor(), author);
        assertEquals(r.getTitle(), title);
        assertEquals(r.getTotalTime(), s.getTime());
        assertEquals(r.getSteps(), steps);
        assertEquals(r.getIngredients(), s.getIngredients());
    }
    @Test
    public void testEquals() {
        List<Step> steps1 = new ArrayList<Step>();
        steps1.add(StepUnitTest.createGenericStep(0, 0, 0, 1));
        List<Step> steps2 = new ArrayList<Step>();
        steps2.add(StepUnitTest.createGenericStep(2, 0, 0, 1));

        Recipe r1 = new Recipe("My Recipe 1", "Kyle", steps1);
        Recipe r1_match = new Recipe("My Recipe 1", "Kyle", steps1);
        Recipe r2 = new Recipe("My Recipe 1", "Sam", steps1);
        Recipe r3 = new Recipe("My Recipe 2", "Kyle", steps1);
        Recipe r4 = new Recipe("My Recipe 1", "Kyle", steps2);

        assertEquals(r1, r1_match);
        assertFalse(r1.equals(r2));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(r4));

    }
    @Test(expected = NullPointerException.class)
    public void testNonNullExceptionNoAuthor() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(StepUnitTest.createGenericStep(0, 0, 0, 1));
        final Recipe recipe = new Recipe("test recipe", null, steps);
    }
    @Test(expected = NullPointerException.class)
    public void testNonNullExceptionNoTitle() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(StepUnitTest.createGenericStep(0, 0, 0, 1));
        final Recipe recipe = new Recipe(null, "kyle", steps);
    }
    @Test(expected = NullPointerException.class)
    public void testNonNullExceptionNoSteps() {
        final Recipe recipe = new Recipe("test recipe", "kyle", null);
    }
}