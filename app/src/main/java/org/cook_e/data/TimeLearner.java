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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;

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
    @NonNull
    private static List<LearningWeight> weightList;

    public TimeLearner() {
        // TODO: read actual list from storage
        weightList = new ArrayList<LearningWeight>();
    }

    /**
     * Learns the actual time of a step.
     * @param s the step you want to learn
     * @param time the actual time user took to finish this step (in milliseconds)
     * @throws IllegalArgumentException when actual time is negative
     */
    public void learnStep(@NonNull Step s, @NonNull Duration time) throws IllegalArgumentException{
        Objects.requireNonNull(s, "step must not be null");
        Objects.requireNonNull(s, "time must not be null");
        long actualTime = time.getMillis();
        if (actualTime < 0) throw new IllegalArgumentException("time must not be negative");

        // find old weight, create one if not exist
        int hash = s.hashCode();
        int index = searchStep(hash);
        if (index < 0) {
            index = -(index + 1);
            addStep(hash, index);
        }
        LearningWeight lw = weightList.get(index);

        // calculate new weight
        long oldEstimatedTime = (long) (s.getTime().getMillis() * lw.timeWeight);
        double weightChange;
        if (actualTime >= oldEstimatedTime * LEARNING_LIMIT)
            weightChange = LEARNING_LIMIT - 1;
        else if (actualTime * LEARNING_LIMIT <= oldEstimatedTime)
            weightChange = (1 / LEARNING_LIMIT) - 1;
        else {
            weightChange = (actualTime * 1.0 / oldEstimatedTime) - 1;
        }

        lw.timeWeight = lw.timeWeight + lw.timeWeight * weightChange * lw.learnRate;
        lw.learnRate = lw.learnRate * LEARN_RATE_DECAY_RATE;

        // TODO: write new list to storage
    }

    /**
     * Returns the estimated time for a step based on learning result.
     * If step is not learned before, returns the estimate time of that step.
     * @param s the step you need to estimate the time for
     * @return the estimated time (in milliseconds) for that specific step
     */
    @NonNull
    public Duration getEstimatedTime(@NonNull Step s) {
        Objects.requireNonNull(s, "step must not be null");
        int index = searchStep(s.hashCode());
        if (index >= 0) {
            long time = (long) (s.getTime().getMillis() * weightList.get(index).timeWeight);
            return new Duration(time);
        }
        return s.getTime().toDuration();
    }

    /**
     * Clears all data stored in learner
     */
    public void clearLearner() {
        weightList.clear();
        // TODO: write empty list to storage
    }

    /**
     * Searches for the index of the given step in sorted weight list
     * @param hash hash code of the step you want to search for
     * @return index of that step in weight list.
     *              If the step is not in the weight list, return an indicator of the position if
     *              that step is in the list. position = -(return value + 1)
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
        return -start - 1;
    }

    /**
     * Adds the given step to sorted weight list at given index
     * @param hash hash code of the step you want to search
     * @param index index that you want to add to
     * @throws IndexOutOfBoundsException when index is out of bound
     */
    private void addStep(int hash, int index) throws IndexOutOfBoundsException{
        if (index < 0 || index > weightList.size())
            throw new IndexOutOfBoundsException("Index is out of bound");
        LearningWeight lw = new LearningWeight(hash);
        weightList.add(index, lw);
    }

    private class LearningWeight implements Comparable<LearningWeight> {
        public int hash; // the hash code of the step
        public double timeWeight; // learned weight for estimated time of this step
        public double learnRate; // learning rate of this step

        /**
         * Construct a new object for step with hash code hash
         * @param hash hash code of that step
         */
        public LearningWeight(int hash) {
            this.hash = hash;
            this.timeWeight = 1;
            this.learnRate = 1;
        }

        @Override
        public int compareTo(LearningWeight lw) {
            return this.hash - lw.hash;
        }
    }
}
