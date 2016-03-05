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

package org.cook_e.data;

import android.support.annotation.NonNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

/**
 * According to the step and actual time given
 * this class will perform operations to give more accurate estimate time for one user
 */
public class TimeLearner implements TimeLearnerInterface {
    // Maximum multiple of estimated time learner can change each time
    private static final double LEARNING_LIMIT = 2.0;

    // The rate that learn rate decays for each learn
    private static final double LEARN_RATE_DECAY_RATE = 0.75;

    // Sorted list of learning weights. List is sorted by hash code
    @NonNull
    private Map<Long, List<LearningWeight>> mWeights;

    private StorageAccessor mStorageAccessor;

    /**
     * The constructor for TimeLearner
     *
     * @param sA the StorageAccessor for this TimeLearner
     * @param b the Bunch users want to cook
     * @throws SQLException
     */
    public TimeLearner(StorageAccessor sA, Bunch b) throws SQLException {
        mStorageAccessor = sA;
        mWeights = mStorageAccessor.loadLearnerData(b);
    }

    /**
     * Learns the actual time of a step.
     *
     * @param r the recipe you want to record the time for
     * @param time the actual time user took to finish this step (in milliseconds)
     * @throws IllegalArgumentException when actual time is negative
     */
    @Override
    public void learnStep(@NonNull Recipe r, Step s, @NonNull Duration time) throws IllegalArgumentException, SQLException{
        Objects.requireNonNull(r, "recipe must not be null");
        Objects.requireNonNull(time, "time must not be null");
        long actualTime = time.getMillis();
        if (actualTime < 0) throw new IllegalArgumentException("time must not be negative");

        LearningWeight lw = accessOrCreateLearningWeight(r, s);

        // calculate new weight
        long oldEstimatedTime = (long) (s.getTime().getMillis() * lw.getTimeWeight());
        double weightChange;
        if (actualTime >= oldEstimatedTime * LEARNING_LIMIT)
            weightChange = LEARNING_LIMIT - 1;
        else if (actualTime * LEARNING_LIMIT <= oldEstimatedTime)
            weightChange = (1 / LEARNING_LIMIT) - 1;
        else {
            weightChange = (actualTime * 1.0 / oldEstimatedTime) - 1;
        }

        lw.setTimeWeight(lw.getTimeWeight() + lw.getTimeWeight() * weightChange * lw.getLearnRate());
        lw.setLearnRate(lw.getLearnRate() * LEARN_RATE_DECAY_RATE);
        mStorageAccessor.updateLearnerData(r, lw);
    }

    /**
     * Helper that finds a learning weight associated with the recipe and step or creates it if it does not exist
     *
     * @param r the recipe you want to record the time for
     * @param s the particular step you want to record the time for
     * @return the LearningWeight of this step
     */
    private LearningWeight accessOrCreateLearningWeight(Recipe r, Step s) {
        List<LearningWeight> weights = mWeights.get(r.getObjectId());

        // mWeights does not contain the learningWeight for this recipe does not exist, so create one
        if (weights == null || weights.size() == 0) {
            int stepCount = r.getSteps().size();
            weights = new ArrayList<>(stepCount);
            for (int i = 0; i < stepCount; i++) {
                weights.add(new LearningWeight(i));
            }
            mWeights.put(r.getObjectId(), weights);
        }

        LearningWeight lw = weights.get(s.getIndex());

        // The learningWeight for this step does not exits
        if (lw == null) {
            lw = new LearningWeight(s.getIndex());
            weights.add(s.getIndex(), lw);
        }
        return lw;
    }
    /**
     * Returns the estimated time for a step based on learning result.
     * If step is not learned before, returns the estimate time of that step.
     *
     * @param s the step you need to estimate the time for
     * @return the estimated time (in milliseconds) for that specific step
     */
    @Override
    @NonNull
    public Duration getEstimatedTime(@NonNull Recipe r, @NonNull Step s) {
        Objects.requireNonNull(r, "recipe must not be null");
        Objects.requireNonNull(s, "step must not be null");
        LearningWeight lw = accessOrCreateLearningWeight(r, s);
        long time = (long) (s.getTime().getMillis() * lw.getTimeWeight());
        return Duration.millis(time);
    }
}
