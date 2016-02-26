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

package org.cook_e.cook_e.ui;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.cook_e.cook_e.R;
import org.cook_e.data.Objects;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * A fragment used in {@link org.cook_e.cook_e.CookActivity} that displays a timer for a
 * simultaneous {@link org.cook_e.data.Step}
 *
 * Activities that use this fragment must implement the
 * {@link org.cook_e.cook_e.ui.TimerFragment.StepFinishListener} interface.
 */
public class TimerFragment extends Fragment {

    /**
     * An interface for things that can be notified when a simultaneous step is finished
     */
    public interface StepFinishListener {
        /**
         * Called when a step is finished.
         *
         * The current implementation calls this method when the timer runs out or when the user
         * presses the done button.
         *
         * @param timerFragment the fragment associated with the completed step
         * @param recipe the recipe that contains the step
         * @param step the step that was completed
         */
        void onStepFinished(TimerFragment timerFragment, Recipe recipe, Step step);
    }

    /**
     * The interval between updates
     */
    private static final Duration INTERVAL = Duration.millis(500);

    /**
     * Key for the step argument
     */
    private static final String ARG_STEP = TimerFragment.class.getName() + ".ARG_STEP";

    /**
     * Key for the recipe argument
     */
    private static final String ARG_RECIPE = TimerFragment.class.getName() + ".ARG_RECIPE";

    /**
     * Key used when saving state to store the remaining time
     */
    private static final String KEY_TIME_REMAINING = TimerFragment.class.getName() + ".KEY_TIME_REMAINING";

    /**
     * The recipe that contains the step being timed
     */
    private Recipe mRecipe;

    /**
     * The step being timed
     */
    private Step mStep;

    /**
     * The remaining countdown time
     */
    private ReadableDuration mRemainingTime;

    /**
     * The text view that displays the remaining time
     */
    private TextView mTimerView;

    /**
     * The formatter used to format remaining time periods
     */
    private final PeriodFormatter mFormatter;

    public TimerFragment() {
        // Required empty public constructor

        mFormatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(1)
                .appendHours()
                .appendLiteral(":")
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendLiteral(":")
                .appendSeconds()
                .toFormatter();
    }

    /**
     * Creates a fragment to act as a timer for a step
     *
     * @param recipe the recipe that contains the step
     * @param step the step to time
     * @return a TimerFragment for the provided step
     * @throws NullPointerException     if any argument is null
     * @throws IllegalArgumentException if the step is not simultaneous or if the recipe does not
     * contain the step
     */
    public static TimerFragment newInstance(Recipe recipe, Step step) {
        Objects.requireNonNull(recipe, "recipe must not be null");
        Objects.requireNonNull(step, "step must not be null");
        if (!recipe.getSteps().contains(step)) {
            throw new IllegalArgumentException("Recipe does not contain step");
        }
        final Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        args.putParcelable(ARG_STEP, step);
        final TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecipe = getArguments().getParcelable(ARG_RECIPE);
        mStep = getArguments().getParcelable(ARG_STEP);
        if (mRecipe == null) {
            throw new IllegalStateException("No recipe argument");
        }
        if (mStep == null) {
            throw new IllegalStateException("No step argument");
        }
        if (savedInstanceState != null) {
            // Restore remaining time
            mRemainingTime = (ReadableDuration) savedInstanceState.getSerializable(KEY_TIME_REMAINING);
        } else {
            // Get duration from step
            mRemainingTime = mStep.getTime();
        }

        // Check parent
        if (!(getActivity() instanceof StepFinishListener)) {
            throw new IllegalStateException("The parent of this fragment must implement StepFinishListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_TIME_REMAINING, mRemainingTime.toDuration());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_timer, container, false);

        mTimerView = (TextView) view.findViewById(R.id.timer_view);

        // Set up description text
        final TextView descriptionView = (TextView) view.findViewById(R.id.description_view);
        descriptionView.setText(mStep.getDescription());

        // Set up done button
        final ImageButton doneButton = (ImageButton) view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify
                notifyStepCompleted();
            }
        });

        // Start timer
        new StepTimer(mRemainingTime).start();

        return view;
    }

    /**
     * Notifies the containing activity that the step has been completed
     */
    private void notifyStepCompleted() {
        // Activity may be null if the fragment was removed early
        // (the timer cannot be canceled)
        final Activity parent = getActivity();
        if (parent != null) {
            ((StepFinishListener) parent).onStepFinished(this, mRecipe, mStep);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateTimer(ReadableDuration remainingTime) {
        // Round the duration down to remove milliseconds
        final Duration roundedTime = roundDownToSecond(remainingTime);
        final Period remainingPeriod = roundedTime.toPeriod();
        mTimerView.setText(mFormatter.print(remainingPeriod));
        mRemainingTime = remainingTime;
    }

    /**
     * Rounds a duration down to the nearest second
     * @param duration the duration to round. Must not be null.
     * @return a duration up to 1 second less than the provided duration, representing an
     * integer number of seconds
     */
    private static Duration roundDownToSecond(ReadableDuration duration) {
        final long millis = duration.getMillis();
        return Duration.millis(millis - (millis % 1000));
    }

    private class StepTimer extends CountDownTimer {

        /**
         * Creates a new StepTimer
         *
         * @param duration the duration to count for
         */
        public StepTimer(ReadableDuration duration) {
            super(duration.getMillis(), INTERVAL.getMillis());
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateTimer(Duration.millis(millisUntilFinished));
        }

        @Override
        public void onFinish() {
            updateTimer(Duration.ZERO);
            notifyStepCompleted();
        }
    }
}
