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

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.cook_e.cook_e.ui.CookStep;
import org.cook_e.cook_e.ui.TimerFragment;
import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.Schedule;
import org.cook_e.data.Step;

public class CookActivity extends AppCompatActivity {
    private static final String TAG = CookActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private Bunch mBunch;
    private Schedule mSchedule;
    private CookStep mCookStep;
    public static final String Bunch = CookActivity.class.getName() + ".Bunch";

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
        setCurrentStep(firstStep, mSchedule.getCurrentStepRecipe());

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
                    setCurrentStep(step, mSchedule.getCurrentStepRecipe());
                }
                return true;

            case R.id.next:
                // User chose the "next" item,
                step = mSchedule.getNextStep();
                if (step != null) {
                    setCurrentStep(step, mSchedule.getCurrentStepRecipe());
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
     */
    private void setCurrentStep(Step step, Recipe recipe) {
        mCookStep.setStep(step, recipe.getTitle());
        Log.d(TAG, "Step: " + step);
        if (step.isSimultaneous()) {
            // Add a timer fragment for the step
            final TimerFragment timerFragment = TimerFragment.newInstance(step);
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.timer_container, timerFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private Bunch getBunch() {
        final Intent intent = getIntent();
        final Bunch meal = intent.getParcelableExtra(Bunch);
        if (meal == null) {
            throw new IllegalStateException("No bunch in intent");
        }
        return meal;
    }
}
