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

package org.cook_e.cook_e;


import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import org.cook_e.cook_e.R;
import org.cook_e.cook_e.ui.RecipeAddListAdapter;
import org.cook_e.data.Objects;
import org.cook_e.data.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An activity that displays a list of recipes and allows the user to add one or more of them
 * to a meal
 */
public class MealRecipeAddActivity extends AppCompatActivity {

    private static final String TAG = MealRecipeAddActivity.class.getSimpleName();

    /**
     * A request code used with {@link android.app.Activity#startActivityForResult(Intent, int)} to
     * request that this activity return the recipes to be added
     */
    public static int REQUEST_ADD_RECIPES = 84;

    /**
     * A key used to identify an intent extra that contains an array of Recipes that should be
     * displayed
     */
    public static String EXTRA_RECIPES = MealRecipeAddActivity.class.getName() + ".RECIPES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_recipe_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();

        // Unpack recipes
        final ObservableArrayList<Recipe> recipes = unpackRecipes();

        Log.d(TAG, "Recipes: " + recipes);

        // Initialize view
        final ListView list = (ListView) findViewById(R.id.recipe_list);
        list.setAdapter(new RecipeAddListAdapter(this, recipes));

        // Set initial result
        final Intent resultIntent = new Intent(Intent.ACTION_DEFAULT);
        resultIntent.putExtra(EXTRA_RECIPES, new Recipe[0]);
        setResult(RESULT_OK, resultIntent);
    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.add_recipes);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the recipes that have been added
        // TODO
    }

    /*
     * This is a workaround for inconsistent behavior.
     *
     * Pressing the system back button or calling finish() returns a result to the parent activity,
     * as expected. However, the default action when the up button is pressed does not send a result
     * to the parent. This override ensures that a result is sent when the action bar up button is
     * pressed.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private ObservableArrayList<Recipe> unpackRecipes() {
        final Parcelable[] parcelables = getIntent().getParcelableArrayExtra(EXTRA_RECIPES);
        Objects.requireNonNull(parcelables, "MealRecipeAddActivity must be started with a recipes extra");
        final ObservableArrayList<Recipe> recipes = new ObservableArrayList<>();
        recipes.ensureCapacity(parcelables.length);
        for (Parcelable parcelable : parcelables) {
            recipes.add((Recipe) parcelable);
        }
        return recipes;
    }

}
