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
                Duration.standardMinutes(1), false);
        final Step step1_2 = new Step(Collections.<String>emptyList(), "Recipe 1 step 2",
                Duration.standardMinutes(1), false);
        final Step step2_1 = new Step(Collections.<String>emptyList(), "Recipe 2 step 1",
                Duration.standardMinutes(1), false);
        final Step step2_2 = new Step(Collections.<String>emptyList(), "Recipe 2 step 2",
                Duration.standardMinutes(1), false);


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
