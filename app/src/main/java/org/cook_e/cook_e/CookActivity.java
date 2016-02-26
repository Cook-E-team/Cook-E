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
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.cook_e.cook_e.ui.CookStep;
import org.cook_e.cook_e.ui.TimerFragment;
import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.Schedule;
import org.cook_e.data.Step;

/**
 * An activity that shows the steps involved in cooking a {@link Bunch}
 *
 * When this activity is started, its intent must include an extra with the key {@link #EXTRA_BUNCH}
 * containing the bunch to be cooked.
 */
public class CookActivity extends AppCompatActivity implements TimerFragment.StepFinishListener {
    /**
     * The tag used for logging
     */
    @SuppressWarnings("unused")
    private static final String TAG = CookActivity.class.getSimpleName();
    /**
     * The extra key used to provide a bunch/meal to cook
     */
    public static final String EXTRA_BUNCH = CookActivity.class.getName() + ".EXTRA_BUNCH";

    /**
     * The bunch/meal being prepared
     */
    private Bunch mBunch;
    /**
     * The schedule
     */
    private Schedule mSchedule;
    /**
     * The cook step fragment, that displays each step
     */
    private CookStep mCookStep;

    /**
     * The number of simultaneous steps with active timers
     */
    private int mActiveSimultaneousSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook);

        mBunch = getBunch();
        mSchedule = new Schedule(mBunch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCookStep = (CookStep) getFragmentManager().findFragmentById(R.id.fragment);
        Step firstStep = mSchedule.getNextStep();
        if (firstStep == null) {
            throw new IllegalStateException("No steps");
        }
        setCurrentStep(firstStep, mSchedule.getCurrentStepRecipe(), false);

        setUpActionBar();
    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(mBunch.getTitle());
        bar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Step step;
        switch (item.getItemId()) {
            case R.id.previous:
                // User chose the "previous" item,
                step = mSchedule.getPrevStep();
                if (step != null) {
                    setCurrentStep(step, mSchedule.getCurrentStepRecipe(), true);
                }
                return true;

            case R.id.next:
                // User chose the "next" item,
                boolean setBefore = mSchedule.getCurrStepIndex() != mSchedule.getMaxVisitedStepIndex();
                step = mSchedule.getNextStep();
                if (step != null) {
                    setCurrentStep(step, mSchedule.getCurrentStepRecipe(), setBefore);
                } else  if (mActiveSimultaneousSteps != 0) {
                    // Explain to the user why they cannot advance
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_title_waiting_for_step)
                            .setMessage(R.string.dialog_waiting_for_step)
                            .show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates the activity to display a step from a recipe
     * @param step the step to display
     * @param recipe the recipe that contains the step
     * @param setBefore whether or not the given step has been set before
     */
    private void setCurrentStep(Step step, Recipe recipe, boolean setBefore) {
        mCookStep.setStep(step, recipe.getTitle());
        if (step.isSimultaneous() && !setBefore) {
            // Add a timer fragment for the step
            final TimerFragment timerFragment = TimerFragment.newInstance(recipe, step);
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.timer_container, timerFragment);
            transaction.commit();

            mActiveSimultaneousSteps++;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private Bunch getBunch() {
        final Intent intent = getIntent();
        final Bunch meal = intent.getParcelableExtra(EXTRA_BUNCH);
        if (meal == null) {
            throw new IllegalStateException("No bunch in intent");
        }
        return meal;
    }

    @Override
    public void onStepFinished(TimerFragment timerFragment, Recipe recipe, Step step) {
        // Remove the fragment
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(timerFragment);
        transaction.commit();
        // Notify the scheduler that the step is done
        mSchedule.finishSimultaneousStepFromRecipe(recipe);
        Toast.makeText(this, "Step \"" + step.getDescription() + "\" finished", Toast.LENGTH_LONG).show();

        mActiveSimultaneousSteps--;
    }
}
