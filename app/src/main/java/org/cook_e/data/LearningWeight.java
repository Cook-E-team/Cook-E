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
public class LearningWeight implements Comparable<LearningWeight> {
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
    public LearningWeight(int hash, double weighted_time, double learn_rate) {
        this.hash = hash;
        timeWeight = weighted_time;
        learnRate = learn_rate;
    }

    @Override
    public int compareTo(LearningWeight lw) {
        return this.hash - lw.hash;
    }
}
