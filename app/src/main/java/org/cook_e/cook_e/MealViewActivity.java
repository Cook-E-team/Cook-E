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

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import org.cook_e.data.Bunch;
import org.cook_e.data.Objects;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.cook_e.data.StorageAccessor;

import java.util.Collections;
import java.util.List;

public class MealViewActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = MealViewActivity.class.getSimpleName();

    /**
     * The extra key used when sending a meal to display in an Intent
     */
    public static final String EXTRA_MEAL = MealViewActivity.class.getName() + ".EXTRA_MEAL";

    /**
     * The recipes in the meal being displayed
     */
    private ObservableArrayList<Recipe> mRecipes;
    /**
     * The meal being displayed
     */
    private Bunch mMeal;

    /**
     * The accessor used to access storage
     */
    private StorageAccessor mAccessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeal = unpackMeal();

        setContentView(R.layout.activity_meal_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();

        // Get recipes
        mRecipes = new ObservableArrayList<>();
        mRecipes.addAll(mMeal.getRecipes());

        // Set up recipe list
        final ListView recipeList = (ListView) findViewById(R.id.recipe_list);
        recipeList.setAdapter(new MealRecipeListAdapter(this, mRecipes));

        // Set up floating action button
        final FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.add_button);
        floatingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open item add view
                final Intent intent = new Intent(MealViewActivity.this, MealRecipeAddActivity.class);

                try {
                    final List<Recipe> availableRecipes = mAccessor.loadAllRecipes();
                    intent.putExtra(MealRecipeAddActivity.EXTRA_RECIPES,
                            availableRecipes.toArray(new Recipe[availableRecipes.size()]));
                    startActivityForResult(intent, MealRecipeAddActivity.REQUEST_ADD_RECIPES);
                }
                catch (Exception e) {
                    new AlertDialog.Builder(MealViewActivity.this)
                            .setTitle("Failed to load recipes")
                            .setMessage(e.getLocalizedMessage())
                            .show();
                }
            }
        });

        // Set up accessor
        mAccessor = App.getAccessor();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MealRecipeAddActivity.REQUEST_ADD_RECIPES && resultCode == RESULT_OK) {
            final Parcelable[] parcelables = data.getParcelableArrayExtra(MealRecipeAddActivity.EXTRA_RECIPES);
            final Recipe[] recipesToAdd = Objects.castArray(parcelables, Recipe[].class);
            // Add recipes
            for (Recipe newRecipe : recipesToAdd) {
                if (!mRecipes.contains(newRecipe)) {
                    mRecipes.add(newRecipe);
                }
            }
            // Save meal to storage
            mMeal.setRecipes(mRecipes);
            try {
                mAccessor.editBunch(mMeal);
            }
            catch (Exception e) {
                new AlertDialog.Builder(MealViewActivity.this)
                        .setTitle("Failed to load recipes")
                        .setMessage(e.getLocalizedMessage())
                        .show();
            }
        }
    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(mMeal.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meal_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start:
                // User chose the "schedule" item,
                final Intent intent = new Intent(MealViewActivity.this, CookActivity.class);
                intent.putExtra(CookActivity.Bunch, mMeal);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Saves the current menu
     * @param outState the state to save to
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO
    }

    /**
     * Creates a list of recipes for testing
     * @return a list of recipes
     */
    private ObservableArrayList<Recipe> createTestRecipes() {
        final ObservableArrayList<Recipe> recipes = new ObservableArrayList<>();

        final Recipe lasagna = new Recipe("Lasagna", "Clamify Flumingaster", Collections.<Step>emptyList());
        recipes.add(lasagna);
        final Recipe pie = new Recipe("Pie", "Clamify Flumingaster", Collections.<Step>emptyList());
        recipes.add(pie);
        final Recipe operaCake = new Recipe("Opera Cake", "Clamify Flumingaster", Collections.<Step>emptyList());
        recipes.add(operaCake);
        final Recipe bananaBread = new Recipe("Banana Bread", "Clamify Flumingaster", Collections.<Step>emptyList());
        recipes.add(bananaBread);
        final Recipe lemonCake = new Recipe("Lemon Cake", "Clamify Flumingaster", Collections.<Step>emptyList());
        recipes.add(lemonCake);
        final Recipe pesto = new Recipe("Pesto", "Clamify Flumingaster", Collections.<Step>emptyList());
        recipes.add(pesto);


        return recipes;
    }

    private Bunch unpackMeal() {
        final Intent intent = getIntent();
        final Bunch meal = intent.getParcelableExtra(EXTRA_MEAL);
        if (meal == null) {
            throw new IllegalStateException("No meal extra in intent");
        }
        return meal;
    }
}
