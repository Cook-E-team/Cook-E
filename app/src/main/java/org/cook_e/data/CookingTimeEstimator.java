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


import java.util.*;

/**
 * A class for estimating cooking times.
 */
public class CookingTimeEstimator {
    private CookingTimeEstimator(Schedule schedule) {}

    /**
     * Calculates and returns the estimated amount of time it would take to cook
     * the given bunch if the given schedule is used.
     *
     * @param schedule the
     * @return
     */
    public static int getOptimizedTime(Schedule schedule) {
        // TODO: Complete the implementation of this function when Schedule is
        // integrated with the Timers.
        return -1;
    }

    /**
     * Calculates and returns the estimated amount of time it would take to cook
     * the given bunch if each recipe is done one after another and no interleaving
     * of steps is done.
     *
     * @param bunch the group of recipes estimate the cook time of
     * @return the estimated cooking time of cooking one recipe after another
     */
    public static int getOriginalTime(Bunch bunch) {
        int totalTime = 0;

        List<Recipe> recipes = bunch.getRecipes();
        for (Recipe recipe : recipes) {
            List<Step> steps = recipe.getSteps();
            for (Step step : steps) {
                totalTime += step.getDurationMinutes();
            }
        }

        return totalTime;
    }
}
