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
 */e org.cook_e.cook_e;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.cook_e.cook_e.R;
import org.cook_e.cook_e.ui.CookStep;
import org.cook_e.data.Bunch;
import org.cook_e.data.Schedule;
import org.cook_e.data.Step;

public class CookActivity extends AppCompatActivity {

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
        mCookStep.setStep(firstStep, mSchedule.getRecipeFromStep(firstStep).getTitle());

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
                    mCookStep.setStep(step, mSchedule.getRecipeFromStep(step).getTitle());
                }
                return true;

            case R.id.next:
                // User chose the "next" item,
                step = mSchedule.getNextStep();
                if (step != null) {
                    mCookStep.setStep(step, mSchedule.getRecipeFromStep(step).getTitle());
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
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
