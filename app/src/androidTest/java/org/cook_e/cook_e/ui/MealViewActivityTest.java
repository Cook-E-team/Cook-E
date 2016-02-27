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

package org.cook_e.cook_e.ui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;

import org.cook_e.cook_e.MealViewActivity;
import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.joda.time.Duration;

import java.util.Arrays;
import java.util.Collections;

/**
 * Tests the {@link org.cook_e.cook_e.MealViewActivity} activity
 */
public class MealViewActivityTest extends ActivityInstrumentationTestCase2<MealViewActivity> {
    public MealViewActivityTest() {
        super(MealViewActivity.class);
    }

    /**
     * A test rule that prevents the activity from being started immediately
     *
     * This allows a Bunch to be provided to the Intent that starts it
     */
    private final ActivityTestRule<MealViewActivity> mActivityRule =
            new ActivityTestRule<>(MealViewActivity.class, true, false);


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create a meal with recipes, with steps
        final Step step1_1 = new Step(Collections.<String>emptyList(), "Recipe 1 step 1",
                Duration.standardMinutes(1), false, 0);
        final Step step1_2 = new Step(Collections.<String>emptyList(), "Recipe 1 step 2",
                Duration.standardMinutes(1), false, 1);
        final Step step2_1 = new Step(Collections.<String>emptyList(), "Recipe 2 step 1",
                Duration.standardMinutes(1), false, 2);
        final Step step2_2 = new Step(Collections.<String>emptyList(), "Recipe 2 step 2",
                Duration.standardMinutes(1), false, 3);


        final Recipe recipe1 = new Recipe("Recipe 1", "Clamify Flumingaster",
                Arrays.asList(step1_1, step1_2));
        final Recipe recipe2 = new Recipe("Recipe 2", "Oysterify Flumingaster",
                Arrays.asList(step2_1, step2_2));

        final Bunch testMeal = new Bunch();
        testMeal.setTitle("Test meal 1");
        testMeal.addRecipe(recipe1);
        testMeal.addRecipe(recipe2);

        // Start activity
        final Intent intent = new Intent();
        intent.putExtra(MealViewActivity.EXTRA_MEAL, testMeal);
        mActivityRule.launchActivity(intent);
    }

    // TODO: Add test methods

}
