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


import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import org.cook_e.cook_e.ui.RecipeAddListAdapter;
import org.cook_e.data.Bunch;
import org.cook_e.data.Objects;
import org.cook_e.data.Recipe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
     * A key used to identify an intent extra that contains an array of Recipes that are already
     * in the meal
     */
    public static String EXIST_RECIPES = MealRecipeAddActivity.class.getName() + ".EXIST";

    /**
     * The recipes the user has selected to add
     */
    private List<Recipe> mSelectedRecipes;

    /**
     * The view used for searching
     */
    private SearchView mSearchView;

    /**
     * Total unpackedRecipes
     */
    private ObservableArrayList<Recipe> mRecipes;

    /**
     * Total recipes that should be shown in the list
     */
    private ObservableArrayList<Recipe> mVisibleRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedRecipes = new ArrayList<>();

        setContentView(R.layout.activity_meal_recipe_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();

        // Unpack recipes
        mVisibleRecipes = new ObservableArrayList<>();
        mRecipes = unpackRecipes();
        mVisibleRecipes.addAll(mRecipes);
        mSelectedRecipes.addAll(existRecipes());

        // Initialize view
        final ListView list = (ListView) findViewById(R.id.recipe_list);
        final RecipeAddListAdapter adapter = new RecipeAddListAdapter(this, mVisibleRecipes, mSelectedRecipes);
        adapter.setAddListener(new RecipeAddListAdapter.RecipeAddListener() {
            @Override
            public void recipeAddRequested(Recipe recipe) {

                Log.d(TAG, "Added recipe " + recipe);
                // Add to list of recipes to add
                if (!mSelectedRecipes.contains(recipe)) {
                    mSelectedRecipes.add(recipe);
                    try {
                        if (!App.getAccessor().containsLocalRecipe(recipe.getObjectId())) {
                            App.getAccessor().storeRecipe(recipe);
                        }
                    } catch (SQLException e) {
                        new AlertDialog.Builder(MealRecipeAddActivity.this)
                                .setTitle("Failed to process recipe")
                                .setMessage(e.getLocalizedMessage())
                                .show();
                    }
                }
                updateResult();
            }
        });
        list.setAdapter(adapter);

        mSearchView = (SearchView) findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(new SearchHandler());

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
    }

    /*
     * This is a workaround for inconsistent behavior.
     *
     * Pressing the system back nextButton or calling finish() returns a result to the parent activity,
     * as expected. However, the default action when the up nextButton is pressed does not send a result
     * to the parent. This override ensures that a result is sent when the action bar up nextButton is
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

    /**
     * Unpacks the recipes existing in the meal from the intent that started this activity and returns a List of those recipes
     * @return list of recipes
     */
    private List<Recipe> existRecipes() {
        final Parcelable[] parcelables = getIntent().getParcelableArrayExtra(MealRecipeAddActivity.EXIST_RECIPES);
        Objects.requireNonNull(parcelables,
                "MealRecipeAddActivity must be started with a exist recipe list");
        final ObservableArrayList<Recipe> exist = new ObservableArrayList<>();
        exist.ensureCapacity(parcelables.length);
        for (Parcelable parcelable : parcelables) {
            exist.add((Recipe) parcelable);
        }
        return exist;
    }

    private class SearchHandler implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            // Do nothing more
            searchRecipes();
            updateVisibleMeals();

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }
    }
    private void searchRecipes() {
        final String query = mSearchView.getQuery().toString();
        Set<Recipe> recipeSet = new HashSet<>();
        recipeSet.addAll(mRecipes);
        List<Recipe> recipes = null;

        try {
            recipes = App.getAccessor().loadRecipes(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recipeSet.addAll(recipes);
        mRecipes.clear();
        mRecipes.addAll(recipeSet);
    }
    /**
     * Update the meals shown on screen.
     * Shows filtered meals if the user types in the search bar or shows all the meals if nothing is entered
     */
    private void updateVisibleMeals() {
        final String query = mSearchView.getQuery().toString();
        // Make all meals visible
        mVisibleRecipes.clear();
        if (!query.isEmpty()) {
            final List<Recipe> filteredRecipes = new ArrayList<>();
            // Limit mVisibleMeals to the meals whose titles contain the query
            // Case insensitive
            final String lowerQuery = query.toLowerCase(Locale.getDefault());

            for (Recipe recipe : mRecipes) {
                final String lowerTitle = recipe.getTitle().toLowerCase(Locale.getDefault());
                if (lowerTitle.contains(lowerQuery)) {
                    filteredRecipes.add(recipe);
                }
            }
            mVisibleRecipes.addAll(filteredRecipes);
        } else {
            // Empty query
            mVisibleRecipes.addAll(mRecipes);
        }
    }

}
