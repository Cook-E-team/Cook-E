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
import org.cook_e.data.Step;
import org.cook_e.data.StorageParser;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/*
 * Unit Tests for the Step class
 */
public class StorageParserTest {
    @Test
    public void testStringToList() {
        List<String> lst = new ArrayList<>();
        lst.add("hello");
        lst.add("goodbye");
        lst.add("test");
        assertEquals(StorageParser.toArrayList("hello,goodbye,test"), lst);
    }

    @Test
    public void testConvertStringToRecipe() {
        StorageParser parser = new StorageParser();
        String stepDesc = "Step{mDescription='lalalal', mTime=600000, " +
                          "mIngredients=ingredients, mSimultaneous=true}\n" +
                          "Step{mDescription='lalalal', mTime=600000, " +
                          "mIngredients=ingredients, mSimultaneous=false}\n";
        List<Step> steps = new ArrayList<>();
        List<String> ing = new ArrayList<>();
        ing.add("ingredients");
        steps.add(new Step(ing, "lalalal",Duration.millis(600000),true));
        steps.add(new Step(ing, "lalalal",Duration.millis(600000),false));
        Recipe recipe = parser.convertStringToRecipe("title", "author", stepDesc);
        assertEquals(recipe.getSteps().get(0), steps.get(0));
        assertEquals(recipe.getSteps().get(1), steps.get(1));
    }

    @Test
    public void testConvertStepToString() {
        StorageParser parser = new StorageParser();
        String stepDesc = "Step{mDescription='lalalal', mTime=600000, " +
                "mIngredients=ingredients, mSimultaneous=true}\n" +
                "Step{mDescription='lalalal', mTime=600000, " +
                "mIngredients=ingredients, mSimultaneous=false}\n";
        List<Step> steps = new ArrayList<>();
        List<String> ing = new ArrayList<>();
        ing.add("ingredients");
        steps.add(new Step(ing,"lalalal",Duration.millis(600000),true));
        steps.add(new Step(ing, "lalalal",Duration.millis(600000),false));
        String ans = parser.convertRecipeToString(new Recipe("title", "author", steps));
        assertEquals(ans, stepDesc);
    }
}
