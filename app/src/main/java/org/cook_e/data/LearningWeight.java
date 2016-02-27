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
 * Created by kylewoo on 2/25/16.
 */
public class LearningWeight  {
    private double timeWeight; // learned weight for estimated time of this step
    private double learnRate; // learning rate of this step
    private int index;

    public LearningWeight(int index) {
        this.timeWeight = 1;
        this.learnRate = 1;
        this.index = index;
    }
    public LearningWeight(int index, double weighted_time, double learn_rate) {
        timeWeight = weighted_time;
        learnRate = learn_rate;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getLearnRate() {
        return learnRate;
    }

    public void setLearnRate(double learnRate) {
        this.learnRate = learnRate;
    }

    public double getTimeWeight() {
        return timeWeight;
    }

    public void setTimeWeight(double timeWeight) {
        this.timeWeight = timeWeight;
    }
}
