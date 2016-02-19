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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class that produces and manages a schedule for a Bunch.
 */
public class Schedule {
    private final Map<Step, Recipe> stepToRecipeMap;
    private final List<Step> finalSteps;
    private int currSelectedFinalStep = -1;

    /**
     * Creates a schedule based on the given Bunch.
     *
     * @param b the Bunch to schedule finalSteps from
     */
    public Schedule(Bunch b) {
        this.finalSteps = new ArrayList<Step>();
        this.stepToRecipeMap = new HashMap<Step, Recipe>();

        // populate stepToRecipeMap.
        // TODO: does not work if two steps are the same but from different recipes
        List<Recipe> recipes = b.getRecipes();
        for(Recipe recipe : recipes) {
            for(Step step : recipe.getSteps()) {
                stepToRecipeMap.put(step, recipe);
            }
        }

        // populate UnscheduledRecipeStepsList
        List<UnscheduledRecipeSteps> unscheduledRecipeStepsList = new ArrayList<UnscheduledRecipeSteps>();
        for (Recipe r: recipes) {
            if(r.getSteps().isEmpty()) continue;
            unscheduledRecipeStepsList.add(new UnscheduledRecipeSteps(r));
        }

        // Schedules the finalSteps.
        while (unscheduledRecipeStepsList.size() > 0) {
            Step nextStep = getNextScheduledStep(unscheduledRecipeStepsList);
            this.finalSteps.add(nextStep);
        }
    }

    /**
     *
     * @param s Step to find it's reciept it belongs to
     *
     * @return the Recipe if there is a Receipt asssociated with such step. Return null otherwise
     */
    public Recipe getRecipeFromStep(Step s) {
        if (stepToRecipeMap.containsKey(s)) {
            return stepToRecipeMap.get(s);

        }
        return null;
    }

    /**
     * After done populating the final scheduled step list. This function will return the nextStep
     *
     * @return The NextStep after the current Step. If already last step, return null
     */
    public Step getNextStep() {
        if (currSelectedFinalStep < getStepCount() - 1) {
            currSelectedFinalStep++;
            return finalSteps.get(currSelectedFinalStep);
        }
        return null;
    }

    /**
     * After done populating the final scheduled step list. This function will return the previous step
     * of the current Step
     *
     * @return The PrevStep before the current Step
     */
    public Step getPrevStep() {
        if (currSelectedFinalStep > 0) {
            currSelectedFinalStep--;
            return finalSteps.get(currSelectedFinalStep);
        }
        return null;
    }

    /**
     * Returns the total number of finalSteps scheduled. This value
     * will not change over the lifetime of the Schedule.
     *
     * @return the total number of finalSteps scheduled
     */
    public int getStepCount() {
        return finalSteps.size();
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
            int nextScheduledStepTime = StepDescriptionParser.getTime(nextScheduledStep.getDescription());
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
                    this.simultaneousToEndTime += StepDescriptionParser.getTime(currStep.getDescription());
                } else if (StepDescriptionParser.isSimultaneous(currStep.getDescription())) {
                    simultaneousSeen = true;
                    this.simultaneousToEndTime = StepDescriptionParser.getTime(currStep.getDescription());
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
            if (StepDescriptionParser.isSimultaneous(nextStep.getDescription())) {
                busyTime = StepDescriptionParser.getTime(nextStep.getDescription());
                this.simultaneousToEndTime -= StepDescriptionParser.getTime(nextStep.getDescription());
                for (Step currStep : this.steps) {
                    if (StepDescriptionParser.isSimultaneous(nextStep.getDescription())) {
                        break;
                    }
                    this.simultaneousToEndTime -= StepDescriptionParser.getTime(currStep.getDescription());
                }
            }

            return nextStep;
        }

        /**
         *
         * @return
         */
        public boolean isEmpty() {
            return this.steps.size() == 0;
        }
    }


}
