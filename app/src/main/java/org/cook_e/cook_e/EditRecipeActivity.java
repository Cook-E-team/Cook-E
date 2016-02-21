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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.joda.time.Duration;

import java.util.Collections;

/**
 * Created by Tyler on 2/4/2016.
 *
 * This is the activity for editing a particular recipe.
 */
public class EditRecipeActivity extends AppCompatActivity {

    /**
     * Intent extra that provides the activity to edit
     */
    public static final String EXTRA_RECIPE = EditRecipeActivity.class.getName() + ".RECIPE";

    /**
     * The recipe being edited
     */
    private Recipe mRecipe;

    /**
     * The steps of the recipe being edited
     */
    private ObservableArrayList<Step> mSteps;

    /*
     * Sets up the main view to edit recipes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        mRecipe = unpackRecipe();
        mSteps = new ObservableArrayList<>();
        mSteps.addAll(mRecipe.getSteps());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipe.getTitle());
        }

        // Set up recipe description
        final TextView descriptionView = (TextView) findViewById(R.id.recipeDescription);
        // TODO: Make clear that this text field contains the author
        descriptionView.setText(mRecipe.getAuthor());
        // TODO: Set recipe image

        final StepListAdapter stepsAdapter = new StepListAdapter(this, mSteps);
        ListView stepsList = (ListView) findViewById(R.id.recipeSteps);
        stepsList.setAdapter(stepsAdapter);

        (findViewById(R.id.stepAdd)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Add a step
                        final Step newStep = new Step(Collections.<String>emptyList(), "",
                                Duration.ZERO, false);
                        mSteps.add(newStep);
                        mRecipe.setSteps(mSteps);
                        stepsAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    /*
     * Sets up the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_recipe_menu, menu);
        MenuItem finishItem = menu.findItem(R.id.finish);
        finishItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    /*
     * Handles action bar items "onClick" events.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.finish) {
            // do stuff...
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Accesses the recipe to be edited
     * @return a recipe from the intent that started this activity
     */
    private Recipe unpackRecipe() {
        final Intent intent = getIntent();
        final Recipe recipe = intent.getParcelableExtra(EXTRA_RECIPE);
        if (recipe == null) {
            throw new IllegalStateException("No activity extra provided in intent");
        }
        return recipe;
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

}
