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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.cook_e.data.Bitmaps;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;

import java.sql.SQLException;
import java.util.Collections;


/**
 * An activity that lets the user create a recipe
 */
public class CreateRecipe extends AppCompatActivity {

    /**
     * The key used when saving and restoring the recipe image
     */
    private static final String KEY_RECIPE_IMAGE = CreateRecipe.class.getName() + ".RECIPE_IMAGE";

    /**
     * Text field for title
     */
    private EditText mTitleField;
    /**
     * Text field for description
     */
    private EditText mAuthorField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // must be call beforehand
        setContentView(R.layout.activity_create_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();

        mTitleField = (EditText) findViewById(R.id.title_field);
        mAuthorField = (EditText) findViewById(R.id.author_field);

        // Continue button
        final Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCreatingRecipe();
            }
        });
    }

    private void continueCreatingRecipe() {
        final String title = mTitleField.getText().toString();
        final String author = mAuthorField.getText().toString();
        if (title.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Empty title")
                    .setMessage("Please enter a recipe title")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        // Create a recipe
        final Recipe recipe = new Recipe(title, author, Collections.<Step>emptyList());

        // Store the recipe
        try {
            App.getAccessor().persistRecipe(recipe);
        } catch (SQLException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Failed to save recipe")
                    .setMessage(e.getLocalizedMessage())
                    .show();
            return;
        }

        // Start editor activity
        final Intent intent = new Intent(this, EditRecipeActivity.class);
        intent.putExtra(EditRecipeActivity.EXTRA_RECIPE, recipe);
        startActivity(intent);
        // Close this activity, so that when the user goes back it will not appear again
        finish();
    }


    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.create_recipe);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.create_recipe, menu);
        return true;
    }
}