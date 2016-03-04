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
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;

import java.sql.SQLException;
import java.util.Collections;


/**
 * An activity that lets the user create a meal
 */
public class CreateMealActivity extends AppCompatActivity {

    /**
     * Text field for title
     */
    private EditText mTitleField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();

        mTitleField = (EditText) findViewById(R.id.title_field);

        // Continue nextButton
        final Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCreatingMeal();
            }
        });

    }


    private void continueCreatingMeal() {
        final String title = mTitleField.getText().toString();
        if (title.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Empty title")
                    .setMessage("Please enter a meal title")
                    .show();
            return;
        }
        // Create a meal
        final Bunch meal = new Bunch(title, Collections.<Recipe>emptyList());

        // Store the meal
        try {
            App.getAccessor().persistBunch(meal);
        } catch (SQLException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Failed to save meal")
                    .setMessage(e.getLocalizedMessage())
                    .show();
            return;
        }

        // Start editor activity
        final Intent intent = new Intent(this, MealViewActivity.class);
        intent.putExtra(MealViewActivity.EXTRA_MEAL, meal);
        startActivity(intent);
        // Close this activity, so that when the user goes back it will not appear again
        finish();
    }


    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.create_meal);
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
}
