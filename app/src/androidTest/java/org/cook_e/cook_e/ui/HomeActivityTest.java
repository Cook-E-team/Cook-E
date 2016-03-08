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

import android.os.SystemClock;
import android.support.test.espresso.DataInteraction;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import org.cook_e.cook_e.App;
import org.cook_e.cook_e.HomeActivity;
import org.cook_e.cook_e.R;
import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;

import java.sql.SQLException;
import java.util.Collections;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsAnything.anything;

/**
 * Tests basic functionality of {@link org.cook_e.cook_e.HomeActivity}
 */
public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity> {

    /**
     * Number of milliseconds to wait for the ViewPager to completely switch to the selected tab
     */
    private static final int SWITCH_DELAY_MS = 1000;

    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Ensure a meal is in the database
        if (App.getAccessor().loadAllBunches(2).isEmpty()) {
            App.getAccessor().storeBunch(
                    new Bunch("Meal name", Collections.<Recipe>emptyList()));
        }

        getActivity();
    }

    /**
     * Tests the display of tabs on the home page and the selection of tabs by clicking on them
     */
    public void testTabs() {
        final Matcher<View> mealsTab = withText(R.string.meals);
        final Matcher<View> recipesTab = withText(R.string.recipes);
        final Matcher<View> mealList = withTagKey(R.id.test_tag_meal_list, Is.<Object>is("Meal List"));
        final Matcher<View> recipeList = withTagKey(R.id.test_tag_recipe_list, Is.<Object>is("Recipe List"));

        onView(recipesTab).perform(click());
        SystemClock.sleep(SWITCH_DELAY_MS);
        onView(recipesTab).check(matches(isSelected()));
        onView(mealsTab).perform(click());
        SystemClock.sleep(SWITCH_DELAY_MS);
        onView(mealsTab).check(matches(isSelected()));
    }

    /**
     * Tests swiping to change tabs
     */
    public void testTabSwiping() {
        final Matcher<View> mealsTab = withText(R.string.meals);
        final Matcher<View> recipesTab = withText(R.string.recipes);
        final Matcher<View> mealList = withTagKey(R.id.test_tag_meal_list,
                Is.<Object>is("Meal List"));
        final Matcher<View> recipeList = withTagKey(R.id.test_tag_recipe_list,
                Is.<Object>is("Recipe List"));
        final Matcher<View> pager = withClassName(IsEqual.equalTo(ViewPager.class.getName()));

        onView(pager).perform(swipeLeft());
        SystemClock.sleep(SWITCH_DELAY_MS);
        onView(recipesTab).check(matches(isSelected()));
        onView(pager).perform(swipeRight());
        SystemClock.sleep(SWITCH_DELAY_MS);
        onView(mealsTab).check(matches(isSelected()));
    }

    /**
     * Tests clicking on a meal in the meal list to open it
     */
    public void testOpenMeal() throws SQLException {
        final Matcher<View> mealList = withTagKey(R.id.test_tag_meal_list,
                Is.<Object>is("Meal List"));
        final DataInteraction mealItem = onData(anything()).inAdapterView(mealList);
        mealItem.perform(click());
        // Check that the MealViewActivity activity has started
        // The below statement produces an error in the instrumentation code.
        // TODO: Fix
//        intended(hasComponent(new ComponentName(getInstrumentation().getTargetContext(),
//                MealViewActivity.class)));
    }
}
