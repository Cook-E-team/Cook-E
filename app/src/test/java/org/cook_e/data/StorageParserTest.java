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

import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/*
 * Unit Tests for the Step class
 */
public class StorageParserTest {
    @Test
    public void testStringToList() {
        List<String> lst = new ArrayList<String>();
        lst.add("hello");
        lst.add("goodbye");
        lst.add("test");
        assertEquals(StorageParser.toArrayList("hello,goodbye,test"), lst);
    }

    @Test
    public void testConvertStringToRecipe() {
        StorageParser parser = new StorageParser();
        String stepDesc = "Step{mDescription='lalalal', mTime=10, " +
                          "mIngredients=ingredients, mSimultaneous=true}\n" +
                          "Step{mDescription='lalalal', mTime=10, " +
                          "mIngredients=ingredients, mSimultaneous=false}\n";
        List<Step> steps = new ArrayList<>();
        List<String> ing = new ArrayList<>();
        ing.add("ingredients");
        steps.add(new Step(ing, "", "lalalal",Duration.standardMinutes(10),true));
        steps.add(new Step(ing, "", "lalalal",Duration.standardMinutes(10),false));
        Recipe recipe = parser.convertStringToRecipe("title", "author", stepDesc);
        assertEquals(recipe.getSteps().get(0), steps.get(0));
        assertEquals(recipe.getSteps().get(1), steps.get(1));
    }

    @Test
    public void testConvertStepToString() {
        StorageParser parser = new StorageParser();
        String stepDesc = "Step{mDescription='lalalal', mTime=PT600S, " +
                "mIngredients=ingredients, mSimultaneous=true}\n" +
                "Step{mDescription='lalalal', mTime=PT600S, " +
                "mIngredients=ingredients, mSimultaneous=false}\n";
        List<Step> steps = new ArrayList<>();
        List<String> ing = new ArrayList<>();
        ing.add("ingredients");
        steps.add(new Step(ing, "", "lalalal",Duration.standardMinutes(10),true));
        steps.add(new Step(ing, "", "lalalal",Duration.standardMinutes(10),false));
        String ans = parser.convertRecipeToString(new Recipe("title", "author", steps));
        assertEquals(ans, stepDesc);
    }
}
