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
import java.util.List;

/**
 * A class that produces and manages a schedule for a Bunch.
 */
public class Schedule {
    private final List<Recipe> recipes;
    private final List<Step> steps;

    /**
     * Creates a schedule based on the given Bunch.
     *
     * @param b the Bunch to schedule steps from
     */
    public Schedule(Bunch b) {
        this.recipes = b.getRecipes();
        this.steps = new ArrayList<Step>();

        // Each UnscheduledSteps element is the the list of unscheduled steps for
        // a particular recipe.
        List<UnscheduledSteps> unscheduledRecipeSteps = new ArrayList<UnscheduledSteps>();
        for (Recipe r: recipes) {
            unscheduledRecipeSteps.add(new UnscheduledSteps(r));
        }

        // Schedules the steps.
        while (unscheduledRecipeSteps.size() > 0) {
            Step nextStep = getNextScheduledStep(unscheduledRecipeSteps);
            this.steps.add(nextStep);
        }
    }

    /**
     * Returns the total number of steps scheduled. This value
     * will not change over the lifetime of the Schedule.
     *
     * @return the total number of steps scheduled
     */
    public int getStepCount() {
        return steps.size();
    }

    /**
     * Returns the step at the given index. If index is < 0 or
     * >= step count, then an IndexOutOfBoundsException is is thrown.
     *
     * @param index the step to return
     * @return the step at the given index
     */
    public Step getStep(int index) {
        if (index < 0 || index >= steps.size())
            throw new IndexOutOfBoundsException(index + " is not a valid step index." +
                    " There are " + steps.size() + " steps.");

        return steps.get(index);
    }


    /*
     * Removes and returns the step to schedule for shortest cooking time.
     * The given steps is also modified such that each element in the list
     * has its busy time shifted properly.
     *
     * @param steps the unscheduled steps to pick a next step from
     * @return the next step to schedule for shortest cooking time
     */
    private Step getNextScheduledStep(List<UnscheduledSteps> steps) {

        // Finds the recipe with the longest time from the first simultaneous step
        // to the last step that is ready and the recipe with the shortest busy time
        // that is busy.
        int bestReadyIndex = -1;
        int maxSimultaneousToEndTime = -1;
        int bestBusyIndex = -1;
        int minBusyTime = -1;
        for (int i = 0; i < steps.size(); i++) {
            UnscheduledSteps currSteps = steps.get(i);
            if (currSteps.busyTime == 0) {
                // handles ready case
                int currSimultaneousToEndTime = currSteps.getSimultaneousToEndTime();
                if (bestReadyIndex == -1 || currSimultaneousToEndTime > maxSimultaneousToEndTime) {
                    bestReadyIndex = i;
                    maxSimultaneousToEndTime = currSimultaneousToEndTime;
                }
            } else {
                // handles busy case
                if (bestBusyIndex == -1 || currSteps.busyTime < minBusyTime) {
                    bestBusyIndex = i;
                    minBusyTime = currSteps.busyTime;
                }
            }
        }

        // Removes and returns the chosen step.
        Step nextScheduledStep;
        if (bestReadyIndex == -1) {
            // no ready steps case
            Step busyStep = steps.get(bestBusyIndex).removeNextStep();
            int readyStepTime = minBusyTime + StepDescriptionParser.getTime(busyStep.getDescription());

            // TO-DO: update busy time values

            if (steps.get(bestBusyIndex).isEmpty()) {
                steps.remove(bestBusyIndex);
            }
            nextScheduledStep = busyStep;
        } else {
            // ready step case
            Step readyStep = steps.get(bestReadyIndex).removeNextStep();
            int readyStepTime = StepDescriptionParser.getTime(readyStep.getDescription());

            // TO-DO: update busy time values

            if (steps.get(bestReadyIndex).isEmpty()) {
                steps.remove(bestReadyIndex);
            }
            nextScheduledStep = readyStep;
        }
        return nextScheduledStep;
    }

    /**
     * A private helper class used in the process of generating
     * the schedule.
     */
    private class UnscheduledSteps {
        // The list of unscheduled steps.
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
        public UnscheduledSteps(Recipe r) {
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
                    this.simultaneousToEndTime += StepDescriptionParser.getTime(currStep.getDescription());
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
                // recalculate simultaneousToEndTime
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
