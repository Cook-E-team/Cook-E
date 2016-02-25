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


import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.repacked.apache.commons.lang3.StringUtils;

import org.cook_e.cook_e.R;
import org.cook_e.data.Objects;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

/**
 * A fragment used in {@link org.cook_e.cook_e.CookActivity} that displays a timer for a
 * simultaneous {@link org.cook_e.data.Step}
 */
public class TimerFragment extends Fragment {

    /**
     * The interval between updates
     */
    private static final Duration INTERVAL = Duration.millis(500);

    /**
     * Key for the step argument
     */
    private static final String ARG_STEP = TimerFragment.class.getName() + ".ARG_STEP";

    /**
     * Key used when saving state to store the remaining time
     */
    private static final String KEY_TIME_REMAINING = TimerFragment.class.getName() + ".KEY_TIME_REMAINING";

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

    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a fragment to act as a timer for a step
     *
     * @param step the step to time
     * @return a TimerFragment for the provided step
     * @throws NullPointerException     if the step is null
     * @throws IllegalArgumentException if the step is not simultaneous
     */
    public static TimerFragment newInstance(Step step) {
        Objects.requireNonNull(step, "step must not be null");
        final Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        final TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStep = getArguments().getParcelable(ARG_STEP);
        if (savedInstanceState != null) {
            // Restore remaining time
            mRemainingTime = (ReadableDuration) savedInstanceState.getSerializable(KEY_TIME_REMAINING);
        } else {
            // Get duration from step
            mRemainingTime = mStep.getTime();
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

        // Start timer
        new StepTimer(mRemainingTime).start();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateTimer(Duration remainingTime) {
        mTimerView.setText(remainingTime.toString());
        mRemainingTime = remainingTime;
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
        }
    }
}
