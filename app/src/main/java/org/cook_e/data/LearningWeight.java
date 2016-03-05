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

/**
 * This class defines the Learning Weight of a step
 *
 * Created by kylewoo on 2/25/16.
 */
public class LearningWeight  {

    // learned weight for estimated time of this step
    private double timeWeight;

    // learning rate of this step
    private double learnRate;

    // index of this step in the recipe
    private int index;

    /**
     * Constructor of the Learning Weight.
     *
     * @param index the index of this step in the recipe
     */
    public LearningWeight(int index) {
        this(index, 1, 1);
    }

    /**
     * Constructor of the Learning Weight.
     *
     * @param index the index of this step in the recipe
     * @param weighted_time the weighted_time of this step
     * @param learn_rate the learn_rate of this step
     */
    public LearningWeight(int index, double weighted_time, double learn_rate) {
        timeWeight = weighted_time;
        learnRate = learn_rate;
        this.index = index;
    }

    /**
     * @return the index of this step
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index of this step
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return return the learnRate of this step
     */
    public double getLearnRate() {
        return learnRate;
    }

    /**
     * Set the new learnRate
     * @param learnRate the new learnRate to be set
     */
    public void setLearnRate(double learnRate) {
        this.learnRate = learnRate;
    }

    /**
     * @return return the timeWeight of this step
     */
    public double getTimeWeight() {
        return timeWeight;
    }

    /**
     * Set the new timeWeight
     * @param timeWeight the new timeWeight to be set
     */
    public void setTimeWeight(double timeWeight) {
        this.timeWeight = timeWeight;
    }
}
