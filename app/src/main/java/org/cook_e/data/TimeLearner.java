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

package org.cook_e.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * According to the step and actual time given
 * this class will perform operations to give more accurate estimate time for one user
 */
public class TimeLearner {
    // Maximum multiple of estimated time learner can change each time
    private static final double LEARNING_LIMIT = 2.0;

    // The rate that learn rate decays for each learn
    private static final double LEARN_RATE_DECAY_RATE = 0.75;

    // Sorted list of learning weights. List is sorted by hash code
    private static List<LearningWeight> weightList;

    public TimeLearner() {
        // TODO: read actual list from storage
        weightList = new ArrayList<LearningWeight>();
    }

    /**
     * Learns the actual time of a step.
     * @param s the step you want to learn
     * @param actualTime the actual time user took to finish this step (in milliseconds)
     */
    public void learnStep(@NonNull Step s, long actualTime) throws IllegalArgumentException{
        Objects.requireNonNull(s, "step must not be null");
        if (actualTime < 0) throw new IllegalArgumentException("time must not be negative");

        // find old weight, create one if not exist
        int hash = s.hashCode();
        int index = searchStep(hash);
        if (index == -1) {
            index = addStep(hash);
        }
        LearningWeight lw = weightList.get(index);

        // calculate new weight
        long oldEstimatedTime = (long) (s.getTime().getMillis() * lw.timeWeight);
        double weight;
        if (actualTime >= oldEstimatedTime * LEARNING_LIMIT)
            weight = LEARNING_LIMIT - 1;
        else if (actualTime * LEARNING_LIMIT <= oldEstimatedTime)
            weight = (1 / LEARNING_LIMIT) - 1;
        else {
            weight = (actualTime / oldEstimatedTime) - 1;
        }
        lw.timeWeight = lw.timeWeight + lw.timeWeight * weight * lw.learnRate;
        lw.learnRate = lw.learnRate * LEARN_RATE_DECAY_RATE;

        // TODO: write new list to storage
    }

    /**
     * Returns the estimated time for a step based on learning result.
     * If step is not learned before, returns the estimate time of that step.
     * @param s the step you need to estimate the time for
     * @return the estimated time (in milliseconds) for that specific step
     */
    public long getEstimatedTime(@NonNull Step s) {
        Objects.requireNonNull(s, "step must not be null");
        int index = searchStep(s.hashCode());
        if (index != -1)
            return (long) (s.getTime().getMillis() * weightList.get(index).timeWeight);
        return s.getTime().getMillis();
    }

    /**
     * Searches for the index of the given step in sorted weight list
     * @param hash hash code of the step you want to search for
     * @return index of that step in weight list, or -1 if it is not in the list
     */
    private int searchStep(int hash) {
        int start = 0;
        int end = weightList.size() - 1;
        while (start <= end) {
            int cur = (start + end) / 2;
            int curHash = weightList.get(cur).hash;
            if (hash == curHash) return cur;
            else if (hash < curHash) end = cur - 1;
            else start = cur + 1;
        }
        return -1;
    }

    /**
     * Adds the given step to sorted weight list
     * @param hash hash code of the step you want to search.
     *             This step must not be already in the list
     * @return index of the place that step is added
     */
    private int addStep(int hash) {
        LearningWeight lw = new LearningWeight(hash);
        for (int i = 0; i < weightList.size(); i++) {
            if (hash < weightList.get(i).hash) {
                weightList.add(i, lw);
                return i;
            }
        }
        weightList.add(lw);
        return weightList.size() - 1;
    }


}
