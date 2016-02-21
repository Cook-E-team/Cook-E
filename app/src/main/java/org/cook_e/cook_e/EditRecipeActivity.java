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
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.cook_e.cook_e.ui.StepDialogFragment;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;

import java.sql.SQLException;

/**
 * Created by Tyler on 2/4/2016.
 *
 * This is the activity for editing a particular recipe.
 */
public class EditRecipeActivity extends AppCompatActivity implements StepDialogFragment.StepEditListener {

    /**
     * Intent extra that provides the activity to edit
     */
    public static final String EXTRA_RECIPE = EditRecipeActivity.class.getName() + ".RECIPE";

    /**
     * Key used in bundles when saving and restoring the state of {@link #mRecipe}
     */
    private static final String KEY_RECIPE = EditRecipeActivity.class.getName() + ".RECIPE";
    /**
     * Key used in bundles when saving and restoring the state of {@link #mStepEditIndex}
     */
    private static final String KEY_STEP_EDIT_INDEX = EditRecipeActivity.class.getName() + ".STEP_EDIT_INDEX";


    /**
     * The recipe being edited
     */
    private Recipe mRecipe;

    /**
     * The steps of the recipe being edited
     */
    private ObservableArrayList<Step> mSteps;

    /**
     * The index in {@link #mSteps} of the step currently being edited, or -1 if no step is being
     * edited
     */
    private int mStepEditIndex;

    /*
     * Sets up the main view to edit recipes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        if (savedInstanceState == null) {
            // Get recipe from intent
            mRecipe = unpackRecipe();
            mStepEditIndex = -1;
        } else {
            // Get recipe from saved state
            mRecipe = savedInstanceState.getParcelable(KEY_RECIPE);
            if (mRecipe == null) {
                throw new IllegalStateException("No saved recipe in savedInstanceState");
            }
            mStepEditIndex = savedInstanceState.getInt(KEY_STEP_EDIT_INDEX);
        }
        mSteps = new ObservableArrayList<>();
        mSteps.addAll(mRecipe.getSteps());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipe.getTitle());
        }

        // Set up recipe description
        final TextView authorView = (TextView) findViewById(R.id.recipe_author);
        authorView.setText(mRecipe.getAuthor());
        // Recipe image
        final ImageButton imageView = (ImageButton) findViewById(R.id.recipe_image_button);
        imageView.setImageBitmap(mRecipe.getImage());

        final StepListAdapter stepsAdapter = new StepListAdapter(this, mSteps);
        ListView stepsList = (ListView) findViewById(R.id.recipeSteps);
        stepsList.setAdapter(stepsAdapter);

        (findViewById(R.id.stepAdd)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Set the index at which to insert the step
                        mStepEditIndex = mSteps.size();
                        // Show a dialog to create a step
                        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        final Fragment previous = getFragmentManager().findFragmentByTag("dialog");
                        if (previous != null) {
                            transaction.remove(previous);
                        }
                        transaction.addToBackStack(null);
                        final StepDialogFragment dialog = StepDialogFragment.newInstance(null);
                        dialog.show(transaction, "dialog");
                    }
                }
        );

        // Begin editing a step when the user clicks on it
        stepsAdapter.setStepClickListener(new StepListAdapter.StepClickListener() {
            @Override
            public void onStepClicked(Step step, int index) {
                mStepEditIndex = index;
                // Show a dialog to edit the step
                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                final Fragment previous = getFragmentManager().findFragmentByTag("dialog");
                if (previous != null) {
                    transaction.remove(previous);
                }
                transaction.addToBackStack(null);
                final StepDialogFragment dialog = StepDialogFragment.newInstance(step);
                dialog.show(transaction, "dialog");
            }
        });

        // Update recipe when step list changes
        mSteps.addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {
                updateRecipeSteps();
            }

            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                updateRecipeSteps();
            }

            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                updateRecipeSteps();
            }

            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
                updateRecipeSteps();
            }

            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                updateRecipeSteps();
            }
        });
    }

    @Override
    public void stepEditingFinished(@NonNull Step step) {
        // User finished editing a step
        // Put it in the list
        if (mStepEditIndex == -1) {
            throw new IllegalStateException("mStepEditIndex is -1");
        }
        if (mStepEditIndex > mSteps.size()) {
            throw new IllegalStateException("mStepEditIndex is too large");
        }

        if (mStepEditIndex == mSteps.size()) {
            mSteps.add(step);
        } else {
            mSteps.set(mStepEditIndex, step);
        }
        mStepEditIndex = -1;
    }

    /**
     * Updates the steps in {@link #mRecipe} to equal {@link #mSteps}, and saves mRecipe
     */
    private void updateRecipeSteps() {
        mRecipe.setSteps(mSteps);
        try {
            App.getAccessor().persistRecipe(mRecipe);
        } catch (SQLException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Failed to save")
                    .setMessage(e.getLocalizedMessage())
                    .show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current recipe and edit state
        outState.putParcelable(KEY_RECIPE, mRecipe);
        outState.putInt(KEY_STEP_EDIT_INDEX, mStepEditIndex);
    }

    /**
     * Accesses the recipe to be edited
     *
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
