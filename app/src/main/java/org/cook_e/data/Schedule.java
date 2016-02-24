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
   // private final Map<Step, Recipe> stepToRecipeMap;
    private final List<Step> finalSteps;
    private int currSelectedFinalStep = -1;
    private final List<UnscheduledRecipeSteps> unscheduledRecipeStepsList;
    private final int totalStepCount;
    private List<Recipe> finalStepMapToRecipe;

    /**
     * Creates a schedule based on the given Bunch.
     *
     * @param b the Bunch to schedule finalSteps from
     */
    public Schedule(Bunch b) {
        this.finalSteps = new ArrayList<>();
        // this.stepToRecipeMap = new HashMap<>();
        this.finalStepMapToRecipe = new ArrayList<>();

        // populate stepToRecipeMap.
        // TODO: does not work if two steps are the same but from different recipes
        int totalStepCount = 0;
        List<Recipe> recipes = b.getRecipes();
        for(Recipe recipe : recipes) {
            for(Step step : recipe.getSteps()) {
                // stepToRecipeMap.put(step, recipe);
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
     * Returns the Recipe associated with the given Step.
     *
     * @param s Step to find it's recipe it belongs to
     * @return the Recipe if there is a Receipt associated with such step. Return null otherwise
     */
    // public Recipe getRecipeFromStep(Step s) {
    //    return stepToRecipeMap.get(s);
    // }

    // Change the top method to be get receipe from Current index
    // Call to get the mother recipe for the current Step display on the scereen.

    /**
     *
     *
     * @return the receipt current step belongs to
     */
    public Recipe getRecipeFromCurrentIndex() {
        return finalStepMapToRecipe.get(currSelectedFinalStep);
    }


    /**
     * This function returns the next step. Calling this function implies that
     * the current step has been completed if it is a non-simultaneous task. If
     * it a simultaneous task, then it is the callers job to call finishStep and
     * pass in the Step when it has been completed.
     *
     * @return The next step after the current step. If it's already at the final step
     * or no other steps can be scheduled, then null is returned.
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
            ScheduledStep ss = getNextScheduledStep(unscheduledRecipeStepsList);
            nextStep = ss.getStep();

            // nextStep = getNextScheduledStep(unscheduledRecipeStepsList);
            this.finalSteps.add(nextStep);
        }
        return nextStep;
    }

    /**
     * After done populating the final scheduled step list. This function will return the previous step
     * of the current Step. If there is no previous step, then null is returned.
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
     *
     *
     * @param step
     */
    public void finishStep(Step step) {
        // TODO: Implement this function once we can get the UnscheduledRecipeStep associated
        // with the given Step.
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
    private ScheduledStep getNextScheduledStep(List<UnscheduledRecipeSteps> unscheduledRecipeStepsList) {

        // Finds the recipe with the longest time from the first simultaneous step
        // to the last step that is ready.
        int chosenIndex = -1;
        int maxSimultaneousToEndTime = -1;
        for (int i = 0; i < unscheduledRecipeStepsList.size(); i++) {
            UnscheduledRecipeSteps currSteps = unscheduledRecipeStepsList.get(i);
            if (currSteps.isReady()) {
                // if the recipe is ready, then check if it is the new best choice
                // and update accordingly
                int currSimultaneousToEndTime = currSteps.getSimultaneousToEndTime();
                if (currSimultaneousToEndTime > maxSimultaneousToEndTime) {
                    chosenIndex = i;
                    maxSimultaneousToEndTime = currSimultaneousToEndTime;
                }
            }
        }

        Step nextScheduledStep = null;
        if (chosenIndex != -1) {
            // Handles case where one or more recipes were ready by removing and
            // returning the chosen step.
            nextScheduledStep = unscheduledRecipeStepsList.get(chosenIndex).removeNextStep();
            if (unscheduledRecipeStepsList.get(chosenIndex).isEmpty()) {
                unscheduledRecipeStepsList.remove(chosenIndex);
            }
        }
        return new ScheduledStep(nextScheduledStep, unscheduledRecipeStepsList.get(chosenIndex).motherReceipe);
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
        // Whether or not the recipe is ready or not. A recipes isn't
        // ready if a simultaneous step is in progress.
        private boolean isReady;

        public Recipe motherReceipe;


        /**
         * Creates an UnscheduledRecipeSteps object based on the given Recipe.
         *
         * @param r the Recipe to get steps from
         */
        public UnscheduledRecipeSteps(Recipe r) {
            this.steps = r.getSteps();
            this.isReady = true;
            this.motherReceipe = r;

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
         * Returns the time in seconds from the first simultaneous step to the end of the last step.
         *
         * @return time in seconds from the first simultaneous step to the end of the last step.
         */
        public int getSimultaneousToEndTime() {
            return simultaneousToEndTime;
        }

        /**
         * Removes and returns the next step from the unscheduled steps. If there are no
         * steps left, then null is returned.
         *
         * @return the next step if there are any ready ones left, otherwise null
         */
        public Step removeNextStep() {
            if (this.isEmpty() || !this.isReady()) {
                return null;
            }

            Step nextStep = this.steps.remove(0);
            if (nextStep.isSimultaneous()) {
                this.isReady = false;
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
         * Returns true if the previously removed step has been completed
         * and the next steps are ready.
         *
         * @return true if the next steps are ready to be done
         */
        public boolean isReady() {
            return this.isReady;
        }

        /**
         * Sets the next steps as ready to be completed.
         */
        public void setReady() {
            this.isReady = true;
        }

        /**
         * Returns if there are any unscheduled steps left.
         *
         * @return true if there are unscheduled steps left, false otherwise
         */
        public boolean isEmpty() {
            return this.steps.isEmpty();
        }
    }

    private class ScheduledStep {
        private Step step;
        private Recipe motherRecipe;

        public ScheduledStep(Step s, Recipe r) {
            this.step = s;
            this.motherRecipe = r;
        }

        public Step getStep() {
            return step;
        }

        public Recipe getRecipe() {
            return motherRecipe;
        }
    }
}
