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
import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.StepUnitTest;

import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BunchUnitTest {
    @Test
    public void testCreation() {
        List<Recipe> recipes = new ArrayList<Recipe>();
        Recipe r = new Recipe("My Recipe", "Kyle", StepUnitTest.createGenericStep(0, 0, 0, 1));
        recipes.add(r);
        Bunch b = new Bunch("My Bunch", recipes);
        assertEquals(b.getTitle(), "My Bunch");
        assertEquals(b.getRecipes(), recipes);
    }
    @Test
    public void testEquals() {
        List<Recipe> recipes = new ArrayList<Recipe>();
        Recipe r = new Recipe("My Recipe", "Kyle", StepUnitTest.createGenericStep(0, 0, 0, 1));
        recipes.add(r);
        Bunch b1 = new Bunch("My Bunch", recipes);
        Bunch b1_match = new Bunch("My Bunch", recipes);
        Bunch b2 = new Bunch("Bunch 2", recipes);
        assertEquals(b1, b1_match);
        assertFalse(b1.equals(b2));

    }
}
