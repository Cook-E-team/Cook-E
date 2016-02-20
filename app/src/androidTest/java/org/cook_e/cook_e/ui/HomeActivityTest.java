/*
 * Copyright 2016 the Cook-E development team
 *
 *  This file is part of Cook-E.
 *
 *  Cook-E is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Cook-E is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.cook_e.ui;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;

import org.cook_e.cook_e.HomeActivity;
import org.cook_e.cook_e.R;
import org.hamcrest.Matcher;

/**
 * Tests basic functionality of {@link org.cook_e.cook_e.HomeActivity}
 */
public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity> {
    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
        SystemClock.sleep(500);
        onView(recipesTab).check(matches(isSelected()));
        onView(recipeList).check(matches(isCompletelyDisplayed()));
        onView(mealsTab).perform(click());
        SystemClock.sleep(500);
        onView(mealsTab).check(matches(isSelected()));
        onView(mealList).check(matches(isCompletelyDisplayed()));
    }
}
