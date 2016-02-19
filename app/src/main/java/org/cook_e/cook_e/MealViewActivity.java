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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import org.cook_e.data.Recipe;
import org.cook_e.data.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MealViewActivity extends AppCompatActivity {
    private static final String TAG = MealViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();
        
        // Create some recipes for testing
        final ObservableArrayList<Recipe> testRecipes = createTestRecipes();

        // Set up recipe list
        final ListView recipeList = (ListView) findViewById(R.id.recipe_list);
        recipeList.setAdapter(new MealRecipeListAdapter(this, testRecipes));

        // Set up floating action button
        final FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.add_button);
        floatingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open item add view
                final Intent intent = new Intent(MealViewActivity.this, MealRecipeAddActivity.class);
                intent.putExtra(MealRecipeAddActivity.EXTRA_RECIPES,
                        testRecipes.toArray(new Recipe[testRecipes.size()]));
                startActivityForResult(intent, MealRecipeAddActivity.REQUEST_ADD_RECIPES);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MealRecipeAddActivity.REQUEST_ADD_RECIPES && resultCode == RESULT_OK) {
            final Parcelable[] parcelables = data.getParcelableArrayExtra(MealRecipeAddActivity.EXTRA_RECIPES);
            Log.d(TAG, "Got recipes to add: " + Arrays.toString(parcelables));
        }
    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle("Meal name");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meal_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.schedule:
                // User chose the "schedule" item,
                final Intent intent = new Intent(MealViewActivity.this, SchedulerActivity.class);
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

        final Bitmap testImage = BitmapFactory.decodeResource(getResources(), R.drawable.test_image_1);
        final Recipe lasagna = new Recipe("Lasagna", "Clamify Flumingaster", Collections.<Step>emptyList());
        lasagna.setImage(testImage);
        recipes.add(lasagna);
        final Recipe pie = new Recipe("Pie", "Clamify Flumingaster", Collections.<Step>emptyList());
        pie.setImage(testImage);
        recipes.add(pie);
        final Recipe operaCake = new Recipe("Opera Cake", "Clamify Flumingaster", Collections.<Step>emptyList());
        operaCake.setImage(testImage);
        recipes.add(operaCake);
        final Recipe bananaBread = new Recipe("Banana Bread", "Clamify Flumingaster", Collections.<Step>emptyList());
        bananaBread.setImage(testImage);
        recipes.add(bananaBread);
        final Recipe lemonCake = new Recipe("Lemon Cake", "Clamify Flumingaster", Collections.<Step>emptyList());
        lemonCake.setImage(testImage);
        recipes.add(lemonCake);
        final Recipe pesto = new Recipe("Pesto", "Clamify Flumingaster", Collections.<Step>emptyList());
        pesto.setImage(testImage);
        recipes.add(pesto);


        return recipes;
    }
}
