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
import org.json.JSONException;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/*
 * Unit Tests for the Step class
 */
public class StorageParserTest {

    @Test
    public void testConvertStringToRecipe() throws ParseException {
        StorageParser parser = new StorageParser();
        final String stepDesc = "[{\"description\":\"lalalal\",\"duration_ms\":600000," +
                "\"ingredients\":[\"ingredients\"],\"simultaneous\":true},{\"description\":" +
                "\"lalalal\",\"duration_ms\":600000,\"ingredients\":[\"ingredients\"]," +
                "\"simultaneous\":false}]";
        List<Step> steps = new ArrayList<>();
        List<String> ing = new ArrayList<>();
        ing.add("ingredients");
        steps.add(new Step(ing, "lalalal",Duration.millis(600000),true));
        steps.add(new Step(ing, "lalalal",Duration.millis(600000),false));
        final List<Step> parsedSteps = parser.parseRecipeSteps(stepDesc);
        Recipe recipe = new Recipe("Title", "Author", parsedSteps);
        assertEquals(recipe.getSteps().get(0), steps.get(0));
        assertEquals(recipe.getSteps().get(1), steps.get(1));
    }

    @Test
    public void testNoSteps() throws ParseException {
        checkRoundTrip(Collections.<Step>emptyList());
    }

    @Test
    public void testOneStepNoIngredients() throws ParseException {
        checkRoundTrip(Collections.singletonList(
                new Step(Collections.<String>emptyList(), "Description of a step",
                        Duration.standardMinutes(32), true)));
    }

    @Test
    public void testThreeStepsSeveralIngredients() throws ParseException {
        final Step stepA1 = new Step(Arrays.asList("250 mL white truffle oil",
                "100 mL anchovy licorice sauce"), "Step A 1", Duration.standardSeconds(93));
        final Step stepA2 = new Step(Arrays.asList("1 anchovy fillet", "50 mL fish sauce"),
                "Step A 2", Duration.standardMinutes(9));
        final Step stepA3 = new Step(Collections.singletonList("40 cloves of garlic"),
                "Step A 3", Duration.standardMinutes(21));

        final List<Step> steps = Arrays.asList(stepA1, stepA2, stepA3);
        checkRoundTrip(steps);
    }

    /**
     * Serializes a list of steps, then deserializes it and checks that the deserialized list is equal
     * to the provided list
     * @param steps the steps to check
     */
    private void checkRoundTrip(List<Step> steps) throws ParseException {
        final StorageParser parser = new StorageParser();
        final String serialized = parser.serializeRecipeSteps(steps);
        final List<Step> deserialized = parser.parseRecipeSteps(serialized);
        assertEquals(steps, deserialized);
    }
}
