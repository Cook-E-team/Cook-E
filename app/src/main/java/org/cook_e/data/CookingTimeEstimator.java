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
     * the given schedule. The given schedule should be brand new and will be
     * modified by the function.
     *
     * @param schedule the schedule to measure the estimated time of
     * @return the estimated amount of time it would take to cook the given schedule
     */
    public static int getOptimizedTime(Schedule schedule, TimeLearnerInterface timeLearner) {
        int totalTime = 0;
        Map<Recipe, Integer> busyTimes = new HashMap<Recipe, Integer>();
        for (int i = 0; i < schedule.getStepCount(); i++) {
            Step currStep = schedule.getNextStep();
            if (currStep == null) {
                // handles case where all remaining recipes are blocked
                int minBusyTime = Integer.MAX_VALUE;
                for (Recipe recipe : busyTimes.keySet()) {
                    minBusyTime = Math.min(minBusyTime, busyTimes.get(recipe));
                }
                updateBusyTimes(busyTimes, minBusyTime, schedule);
                totalTime += minBusyTime;
                i--;// retry the current step
            } else {
                Recipe currRecipe = schedule.getCurrentStepRecipe();
                if (currStep.isSimultaneous()) {
                    // handles case where the next step is simultaneous
                    busyTimes.put(schedule.getCurrentStepRecipe(),
                            (int) timeLearner.getEstimatedTime(currRecipe, currStep).getStandardMinutes());
                } else {
                    // handles case where the next step is not simultaneous
                    int currStepTime = (int) timeLearner.getEstimatedTime(currRecipe, currStep).getStandardMinutes();
                    updateBusyTimes(busyTimes, currStepTime, schedule);
                    totalTime += currStepTime;
                }
            }
        }

        // adds the maximum remaining busy time into the total time
        int maxRemainingBusyTime = 0;
        for (Recipe recipe : busyTimes.keySet()) {
            maxRemainingBusyTime = Math.max(maxRemainingBusyTime, busyTimes.get(recipe));
        }
        totalTime += maxRemainingBusyTime;

        return totalTime;
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

    /**
     *
     *
     * @param busyTimes the map of recipes to the amount of busy time left to update
     * @param offset the amount to decrease the busy times by
     * @param schedule the schedule to update when recipes busy times hit zero
     */
    private static void updateBusyTimes(Map<Recipe, Integer> busyTimes, int offset, Schedule schedule) {
        Iterator<Map.Entry<Recipe, Integer>> busyTimesIterator = busyTimes.entrySet().iterator();
        while (busyTimesIterator.hasNext()) {
            Map.Entry<Recipe, Integer> currBusyTimeEntry = busyTimesIterator.next();
            Recipe recipe = currBusyTimeEntry.getKey();
            int busyTime = currBusyTimeEntry.getValue();

            busyTimes.put(recipe, Math.max(busyTime - offset, 0));
            if (busyTimes.get(recipe) == 0) {
                busyTimesIterator.remove();
                schedule.finishSimultaneousStepFromRecipe(recipe);
            }
        }
    }
}
