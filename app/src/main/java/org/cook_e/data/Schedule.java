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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that produces and manages a schedule for a Bunch.
 */
public class Schedule {
    private final Map<Step, Recipe> stepToRecipeMap;
    private final List<Step> finalSteps;
    private int currSelectedFinalStep = -1;
    private final List<UnscheduledRecipeSteps> unscheduledRecipeStepsList;
    private final int totalStepCount;

    /**
     * Creates a schedule based on the given Bunch.
     *
     * @param b the Bunch to schedule finalSteps from
     */
    public Schedule(Bunch b) {
        this.finalSteps = new ArrayList<>();
        this.stepToRecipeMap = new HashMap<>();

        // populate stepToRecipeMap.
        // TODO: does not work if two steps are the same but from different recipes
        int totalStepCount = 0;
        List<Recipe> recipes = b.getRecipes();
        for(Recipe recipe : recipes) {
            for(Step step : recipe.getSteps()) {
                stepToRecipeMap.put(step, recipe);
                totalStepCount++;
            }
        }
        this.totalStepCount = totalStepCount;

        // populate UnscheduledRecipeStepsList
        unscheduledRecipeStepsList = new ArrayList<>();
        for (Recipe r: recipes) {
            if(r.getSteps().isEmpty()) continue;
            unscheduledRecipeStepsList.add(new UnscheduledRecipeSteps(r));
        }
    }

    /**
     *
     * @param s Step to find it's recipe it belongs to
     *
     * @return the Recipe if there is a Receipt associated with such step. Return null otherwise
     */
    public Recipe getRecipeFromStep(Step s) {
        return stepToRecipeMap.get(s);
    }

    /**
     * This function returns the next step. Calling this function implies that
     * the current step has been completed if it is a non-simultaneous task.
     *
     * @return The next step after the current step. If it's already at the final step
     * and no other steps can be scheduled, then null is returned.
     */
    public Step getNextStep() {
        Step nextStep = null;
        if (currSelectedFinalStep < finalSteps.size() - 1) {
            // handles the case where the next step has already
            // been scheduled
            currSelectedFinalStep++;
            nextStep = finalSteps.get(currSelectedFinalStep);
        } else if (currSelectedFinalStep == finalSteps.size() -1 &&
                unscheduledRecipeStepsList.size() > 0) {
            // handles the case where the next step hasn't been
            // scheduled yet
            currSelectedFinalStep++;
            nextStep = getNextScheduledStep(unscheduledRecipeStepsList);
            this.finalSteps.add(nextStep);
        }
        return nextStep;
    }

    /**
     * After done populating the final scheduled step list. This function will return the previous step
     * of the current Step
     *
     * @return The previous step before the current Step
     */
    public Step getPrevStep() {
        Step prevStep = null;
        if (currSelectedFinalStep > 0) {
            currSelectedFinalStep--;
            prevStep = finalSteps.get(currSelectedFinalStep);
        }
        return prevStep;
    }

    /**
     * Returns the total number of steps. This value includes both
     * scheduled and unscheduled steps.
     *
     * @return the total number of steps
     */
    public int getStepCount() {
        return totalStepCount;
    }

    /*
     * Removes and returns the step to schedule for shortest cooking time.
     * The given finalSteps is also modified such that each element in the list
     * has its busy time shifted properly.
     *
     * @param finalSteps the unscheduled finalSteps to pick a next step from
     * @return the next step to schedule for shortest cooking time
     */
    private Step getNextScheduledStep(List<UnscheduledRecipeSteps> unscheduledRecipeStepsList) {

        // Finds the recipe with the longest time from the first simultaneous step
        // to the last step that is ready and finds the smallest busy time.
        int chosenIndex = -1;
        int maxSimultaneousToEndTime = -1;
        int minBusyTime = unscheduledRecipeStepsList.get(0).busyTime;
        for (int i = 0; i < unscheduledRecipeStepsList.size(); i++) {
            UnscheduledRecipeSteps currSteps = unscheduledRecipeStepsList.get(i);
            if (currSteps.busyTime < minBusyTime) {
                // updates minBusyTime if we've found a smaller one
                minBusyTime = currSteps.busyTime;
            }
            if (currSteps.busyTime == 0) {
                // if the recipe is ready, then check if it is the new best choice
                // and update accordingly
                int currSimultaneousToEndTime = currSteps.getSimultaneousToEndTime();
                if (currSimultaneousToEndTime > maxSimultaneousToEndTime) {
                    chosenIndex = i;
                    maxSimultaneousToEndTime = currSimultaneousToEndTime;
                }
            }
        }

        if (minBusyTime > 0) {
            // Handles case where all recipes are busy by "fast-forwarding" all recipes
            // by the smallest busy time found and returning the result of a recursive call.
            for (UnscheduledRecipeSteps currUnscheduledRecipeStep : unscheduledRecipeStepsList) {
                currUnscheduledRecipeStep.busyTime = Math.max(
                        currUnscheduledRecipeStep.busyTime - minBusyTime, 0);
            }
            return getNextScheduledStep(unscheduledRecipeStepsList);
        } else {
            // Handles case where one or more recipes were ready by removing and
            // returning the chosen step.
            Step nextScheduledStep = unscheduledRecipeStepsList.get(chosenIndex).removeNextStep();
            int nextScheduledStepTime = nextScheduledStep.getDurationMinutes();
            // Updates busyTimes for all other UnscheduledRecipeSteps
            for (int i = 0; i < unscheduledRecipeStepsList.size(); i++) {
                UnscheduledRecipeSteps currSteps = unscheduledRecipeStepsList.get(i);
                if (i != chosenIndex)
                    currSteps.busyTime = Math.max(currSteps.busyTime - nextScheduledStepTime, 0);
            }
            if (unscheduledRecipeStepsList.get(chosenIndex).isEmpty()) {
                unscheduledRecipeStepsList.remove(chosenIndex);
            }
            return nextScheduledStep;
        }
    }

    /**
     * A private helper class used in the process of generating
     * the schedule.
     */
    private class UnscheduledRecipeSteps {
        // The list of unscheduled finalSteps.
        private final List<Step> steps;
        // The time in seconds from the first simultaneous step to the end of the last step.
        private int simultaneousToEndTime;
        // The time in seconds until the next step can safely be performed. The value
        // of this variable is completely managed by the user of the class.
        public int busyTime;


        /**
         *
         * @param r
         */
        public UnscheduledRecipeSteps(Recipe r) {
            this.steps = r.getSteps();
            this.busyTime = 0;

            // initializes simultaneousToEndTime
            this.simultaneousToEndTime = 0;
            boolean simultaneousSeen = false;
            for (Step currStep : this.steps) {
                if (simultaneousSeen) {
                    this.simultaneousToEndTime += currStep.getDurationMinutes();
                } else if (currStep.isSimultaneous()) {
                    simultaneousSeen = true;
                    this.simultaneousToEndTime = currStep.getDurationMinutes();
                }
            }
        }

        /**
         *
         * @return
         */
        public int getSimultaneousToEndTime() {
            return simultaneousToEndTime;
        }

        /**
         *
         * @return
         */
        public Step removeNextStep() {
            if (this.steps.size() < 1) {
                return null;
            }
            Step nextStep = this.steps.remove(0);
            if (nextStep.isSimultaneous()) {
                busyTime = nextStep.getDurationMinutes();
                this.simultaneousToEndTime -= nextStep.getDurationMinutes();
                for (Step currStep : this.steps) {
                    if (currStep.isSimultaneous()) {
                        break;
                    }
                    this.simultaneousToEndTime -= currStep.getDurationMinutes();
                }
            }

            return nextStep;
        }

        /**
         *
         * @return
         */
        public boolean isEmpty() {
            return this.steps.isEmpty();
        }
    }


}
