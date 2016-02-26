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


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * A pager adapter that provides a meal list fragment in position 0 and a recipe list fragment
 * in position 1
 */
public class HomePageAdapter extends FragmentPagerAdapter {

    /**
     * The recipe list fragment
     */
    @Nullable
    private RecipeList mRecipeList;

    /**
     * The meal list fragment
     */
    @Nullable
    private MealList mMealList;

    /**
     * Creates a new adapter
     * @param fm the fragment manager to use
     */
    public HomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return getMealList();
        } else {
            return getRecipeList();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Ensures that mRecipeList is not null, then returns it
     * @return mRecipeList
     */
    @NonNull
    public RecipeList getRecipeList() {
        if (mRecipeList == null) {
            mRecipeList = new RecipeList();
        }
        return mRecipeList;
    }
    /**
     * Ensures that mMealList is not null, then returns it
     * @return mMealList
     */
    @NonNull
    public MealList getMealList() {
        if (mMealList == null) {
            mMealList = new MealList();
        }
        return mMealList;
    }
}
