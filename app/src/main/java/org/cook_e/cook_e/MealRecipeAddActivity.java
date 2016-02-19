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
import android.widget.ListView;

import org.cook_e.cook_e.ui.RecipeAddListAdapter;
import org.cook_e.data.Objects;
import org.cook_e.data.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that displays a list of recipes and allows the user to add one or more of them
 * to a meal
 *
 * When starting this activity, the parent must provide an extra with a key of {@link #EXTRA_RECIPES}
 * that contains an array of Recipe objects. The user will be able to select zero or more of
 * the provided recipes.
 *
 * The parent should request a result with the request code {@link #REQUEST_ADD_RECIPES} to get a
 * result.
 *
 * This activity will return a result with code {@link #REQUEST_ADD_RECIPES}. The result will contain
 * an extra with the key {@link #EXTRA_RECIPES} containing an array
 * of Recipe objects. The array will contain the recipes that the user selected to add.
 */
public class MealRecipeAddActivity extends AppCompatActivity {

    /**
     * The class tag, used for logging
     */
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

    /**
     * The recipes the user has selected to add
     */
    private List<Recipe> mSelectedRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedRecipes = new ArrayList<>();

        setContentView(R.layout.activity_meal_recipe_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();

        // Unpack recipes
        final ObservableArrayList<Recipe> recipes = unpackRecipes();

        // Initialize view
        final ListView list = (ListView) findViewById(R.id.recipe_list);
        final RecipeAddListAdapter adapter = new RecipeAddListAdapter(this, recipes);
        adapter.setAddListener(new RecipeAddListAdapter.RecipeAddListener() {
            @Override
            public void recipeAddRequested(Recipe recipe) {
                // Verify that the added recipe is actually in the recipe list
                if (BuildConfig.DEBUG) {
                    boolean found = false;
                    for (Recipe existingRecipe : recipes) {
                        if (existingRecipe.equals(recipe)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException(
                                "The recipe added by the user is not in the recipe list");
                    }
                }
                Log.d(TAG, "Added recipe " + recipe);
                // Add to list of recipes to add
                mSelectedRecipes.add(recipe);
                updateResult();
            }
        });
        list.setAdapter(adapter);

        // Set initial result
        updateResult();
    }

    /**
     * Sets up the action bar for this activity
     */
    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.add_recipes);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Updates the result of this activity to contain the contents of {@link #mSelectedRecipes}
     */
    private void updateResult() {
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RECIPES,
                mSelectedRecipes.toArray(new Recipe[mSelectedRecipes.size()]));
        setResult(RESULT_OK, resultIntent);
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

    /**
     * Unpacks the recipes extra from the intent that started this activity and returns an
     * ObservableArrayList containing the same recipes.
     *
     * @return a list of recipes
     */
    private ObservableArrayList<Recipe> unpackRecipes() {
        final Parcelable[] parcelables = getIntent().getParcelableArrayExtra(EXTRA_RECIPES);
        Objects.requireNonNull(parcelables,
                "MealRecipeAddActivity must be started with a recipes extra");
        final ObservableArrayList<Recipe> recipes = new ObservableArrayList<>();
        recipes.ensureCapacity(parcelables.length);
        for (Parcelable parcelable : parcelables) {
            recipes.add((Recipe) parcelable);
        }
        return recipes;
    }

}
