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
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
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
import org.cook_e.data.StorageAccessor;

import java.sql.SQLException;
import java.util.ArrayList;
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

        // Set up floating action nextButton
        final FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.add_button);
        floatingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open item add view
                final Intent intent = new Intent(MealViewActivity.this, MealRecipeAddActivity.class);

                try {
                    final List<Recipe> availableRecipes = mAccessor.loadAllRecipes(App.getDisplayLimit());
                    intent.putExtra(MealRecipeAddActivity.EXIST_RECIPES,
                            mRecipes.toArray(new Recipe[mRecipes.size()]));
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

        // Save meal when recipe list changes
        mRecipes.addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {
                updateRecipes();
            }

            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                updateRecipes();
            }

            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                updateRecipes();
            }

            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
                updateRecipes();
            }

            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                updateRecipes();
            }
        });
    }

    /**
     * Sets the recipes in {@link #mMeal} to {@link #mRecipes}, then saves the meal
     */
    private void updateRecipes() {
        saveMeal();
    }

    /**
     * Tries to save the meal
     */
    private void saveMeal() {
        try {

            // Save recipes that were copied from the remote database but are not saved locally
            // yet
            for (Recipe recipe : mRecipes) {
                if (!mAccessor.containsLocalRecipe(recipe.getObjectId())) {
                    mAccessor.storeRecipe(recipe);
                }
            }
            mMeal.setRecipes(mRecipes);
            mAccessor.persistBunch(mMeal);
        }
        catch (Exception e) {
            new AlertDialog.Builder(MealViewActivity.this)
                    .setTitle("Failed to save meal")
                    .setMessage(e.getLocalizedMessage())
                    .show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // May be returning from another activity that modified recipes.
        // So, reload them.
        try {
            // This code must be careful since the recipe list detects changes
            // and propagates them to the database automatically.
            Bunch freshMeal = mAccessor.loadBunch(mMeal.getTitle());
            mRecipes.clear();
            mRecipes.addAll(freshMeal.getRecipes());
        } catch (SQLException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("Failed to reload meal recipes MealViewActivity.onResume()")
                    .setMessage(e.getLocalizedMessage())
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MealRecipeAddActivity.REQUEST_ADD_RECIPES && resultCode == RESULT_OK) {
            final Parcelable[] parcelables = data.getParcelableArrayExtra(MealRecipeAddActivity.EXTRA_RECIPES);
            final Recipe[] recipesToAdd = Objects.castArray(parcelables, Recipe[].class);
            // Add recipes
            final List<Recipe> addedRecipes = new ArrayList<>();
            for (Recipe newRecipe : recipesToAdd) {
                if (!mRecipes.contains(newRecipe)) {
                    addedRecipes.add(newRecipe);
                }
            }
            mRecipes.addAll(addedRecipes);
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
                int amount = mMeal.getNumOfRecipes();
                if (amount > 0) {
                    final Intent intent = new Intent(MealViewActivity.this, CookActivity.class);
                    intent.putExtra(CookActivity.EXTRA_BUNCH, mMeal);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(MealViewActivity.this)
                            .setMessage("You should have at least one recipe to start cooking.")
                            .show();
                }
                return true;

            case R.id.meal_delete_item:
                // Show a confirmation dialog
                new AlertDialog.Builder(MealViewActivity.this)
                        .setTitle(R.string.question_delete_meal)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteMeal();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Deletes the meal, then exits
     */
    private void deleteMeal() {
        try {
            App.getAccessor().deleteBunch(mMeal);
            finish();
        } catch (SQLException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Failed to delete meal")
                    .setMessage(e.getLocalizedMessage())
                    .show();
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

    private Bunch unpackMeal() {
        final Intent intent = getIntent();
        final Bunch meal = intent.getParcelableExtra(EXTRA_MEAL);
        if (meal == null) {
            throw new IllegalStateException("No meal extra in intent");
        }
        return meal;
    }
}
