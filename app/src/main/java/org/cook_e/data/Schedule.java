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

import android.support.annotation.NonNull;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that produces and manages a schedule for a Bunch.
 */
public class Schedule {
    private final List<ScheduledStep> mScheduledStepList;
    private final List<UnscheduledRecipeSteps> mUnscheduledRecipeStepsList;
    private final int mTotalStepCount;
    private int mCurrScheduledStepIndex = -1;
    private TimeLearnerInterface timeLearner;

    public final int mOriginalEstimatedTime;
    public final int mOptimizedEstimatedTime;


    /**
     * Creates a schedule based on the given Bunch.
     *
     * @param b the Bunch to schedule steps from
     */
    public Schedule(@NonNull Bunch b, @NonNull TimeLearnerInterface timeLearner) {
        this(b, timeLearner, true);
    }

    /**
     * Creates a schedule based on the given Bunch. If the given boolean is true,
     * then estimated times are calculated, otherwise they aren't. This private
     * constructor with the additional boolean is necessary to avoid infinite
     * recursive calls due to calculating estimed times.
     *
     * @param b the Bunch to schedule steps from
     * @param calculateEstimatedTimes whether or not estimated times should be calculated
     */
    private Schedule(@NonNull Bunch b, @NonNull TimeLearnerInterface timeLearner, boolean calculateEstimatedTimes) {
        if (b == null) {
            throw new NullPointerException("Schedule given null bunch.");
        }
        if (timeLearner == null) {
            throw new NullPointerException("Schedule given null timeLearner.");
        }

        if (calculateEstimatedTimes) {
            this.mOriginalEstimatedTime = CookingTimeEstimator.getOriginalTime(b, timeLearner);
            this.mOptimizedEstimatedTime = CookingTimeEstimator.getOptimizedTime(
                    new Schedule(b, timeLearner, false), timeLearner);
        } else {
            this.mOriginalEstimatedTime = -1;
            this.mOptimizedEstimatedTime = -1;
        }

        this.timeLearner = timeLearner;
        this.mScheduledStepList = new ArrayList<>();

        int totalStepCount = 0;
        List<Recipe> recipes = b.getRecipes();
        for(Recipe recipe : recipes) {
            totalStepCount += recipe.getSteps().size();
        }
        this.mTotalStepCount = totalStepCount;

        // populate UnscheduledRecipeStepsList
        this.mUnscheduledRecipeStepsList = new ArrayList<>();
        for (Recipe r: recipes) {
            if(r.getSteps().isEmpty()) continue;
            this.mUnscheduledRecipeStepsList.add(new UnscheduledRecipeSteps(r));
        }
    }

    /**
     * Returns the recipe that contains the current step. If no step has been
     * visited yet, then null is returned.
     *
     * @return the recipe the current step belongs to
     */
    public Recipe getCurrentStepRecipe() {
        Recipe currRecipe = null;
        if (mCurrScheduledStepIndex >= 0 && mCurrScheduledStepIndex < mScheduledStepList.size())
            currRecipe = this.mScheduledStepList.get(this.mCurrScheduledStepIndex).motherRecipe;
        return currRecipe;
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
        if (this.mCurrScheduledStepIndex < this.mScheduledStepList.size() - 1) {
            // handles the case where the next step has already
            // been scheduled
            this.mCurrScheduledStepIndex++;
            nextStep = this.mScheduledStepList.get(this.mCurrScheduledStepIndex).step;
        } else if (this.mCurrScheduledStepIndex == this.mScheduledStepList.size() -1 &&
                this.mUnscheduledRecipeStepsList.size() > 0) {
            // handles the case where the next step hasn't been
            // scheduled yet
            ScheduledStep nextScheduledStep = getNextScheduledStep(this.mUnscheduledRecipeStepsList);
            if (nextScheduledStep != null) {
                this.mCurrScheduledStepIndex++;
                this.mScheduledStepList.add(nextScheduledStep);
                nextStep = nextScheduledStep.step;
            }
        }
        return nextStep;
    }

    /**
     * This function returns the current step. If no step has been
     * visited yet, then null is returned.
     *
     * @return the current step
     */
    public Step getCurrStep() {
        Step currStep = null;
        if (mCurrScheduledStepIndex >= 0 && mCurrScheduledStepIndex < mScheduledStepList.size())
            currStep = mScheduledStepList.get(mCurrScheduledStepIndex).step;
        return currStep;
    }

    /**
     * After done populating the final scheduled step list. This function will return the previous step
     * of the current Step. If there is no previous step, then null is returned.
     *
     * @return The previous step before the current Step
     */
    public Step getPrevStep() {
        Step prevStep = null;
        if (this.mCurrScheduledStepIndex > 0) {
            this.mCurrScheduledStepIndex--;
            prevStep = this.mScheduledStepList.get(this.mCurrScheduledStepIndex).step;
        }
        return prevStep;
    }

    /**
     * Calling this function indicates that the blocking simultaneous step
     * associated with the given recipe has been completed. If no matching recipe
     * is found, then the function silently does nothing. This is largely due to
     * how hard it would be for the caller to know if the particular recipe has
     * has any unscheduled steps left.
     *
     * @param recipe the recipe the finished simultaneous step is associated with
     */
    public void finishSimultaneousStepFromRecipe(Recipe recipe) {
        UnscheduledRecipeSteps matchingRecipeSteps = null;
        for (UnscheduledRecipeSteps currUnscheduledRecipeSteps : mUnscheduledRecipeStepsList) {
            if (currUnscheduledRecipeSteps.motherRecipe.equals(recipe)) {
                currUnscheduledRecipeSteps.setReady();
                return;
            }
        }
    }

    /**
     * Returns whether or not the schedule is at the final step.
     *
     * @return true if and only if the schedule is at the final step
     */
    public boolean isAtFinalStep() {
        return getStepCount() == 0 || getCurrStepIndex() == getStepCount() - 1;
    }

    /**
     * Returns the total number of steps. This value includes both
     * scheduled and unscheduled steps.
     *
     * @return the total number of steps
     */
    public int getStepCount() {
        return this.mTotalStepCount;
    }

    /**
     * Returns the index of the current step. If no step
     * has been visited, then the behavior is undefined.
     *
     * @return the index of the current step
     */
    public int getCurrStepIndex() {
        return mCurrScheduledStepIndex;
    }

    /**
     * Returns the largest index that has been visited. If
     * no step has been visited, then the behavior is undefined.
     *
     * @return the largest index that has been visited
     */
    public int getMaxVisitedStepIndex() {
        return mScheduledStepList.size() - 1;
    }

    /**
     * Removes and returns the step to schedule for shortest cooking time.
     * If no step is ready, then null is returned.

     * @param unscheduledRecipeStepsList the unscheduled recipe steps to pick a next step from
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

        ScheduledStep nextScheduledStep = null;
        if (chosenIndex != -1) {
            // Handles case where one or more recipes were ready by removing and
            // returning the chosen step.
            Step nextStep = unscheduledRecipeStepsList.get(chosenIndex).removeNextStep();
            Recipe motherRecipe = unscheduledRecipeStepsList.get(chosenIndex).motherRecipe;
            nextScheduledStep = new ScheduledStep(nextStep, motherRecipe);

            Log.d("Schedule", "chosenIndex = " + chosenIndex + ", unscheduled steps = " + unscheduledRecipeStepsList);
            if (unscheduledRecipeStepsList.get(chosenIndex).isEmpty()) {
                Log.d("Schedule", "chosenIndex = " + chosenIndex + ", unscheduled steps = " + unscheduledRecipeStepsList);
                unscheduledRecipeStepsList.remove(chosenIndex);
            }
        }
        return nextScheduledStep;
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

        public final Recipe motherRecipe;


        /**
         * Creates an UnscheduledRecipeSteps object based on the given Recipe.
         *
         * @param r the Recipe to get steps from
         */
        public UnscheduledRecipeSteps(Recipe r) {
            this.steps = r.getSteps();
            this.isReady = true;
            this.motherRecipe = r;

            // initializes simultaneousToEndTime
            this.simultaneousToEndTime = 0;
            boolean simultaneousSeen = false;
            for (Step currStep : this.steps) {
                if (simultaneousSeen) {
                    this.simultaneousToEndTime += timeLearner.getEstimatedTime(this.motherRecipe, currStep).getStandardSeconds();
                } else if (currStep.isSimultaneous()) {
                    simultaneousSeen = true;
                    this.simultaneousToEndTime = (int)timeLearner.getEstimatedTime(this.motherRecipe, currStep).getStandardSeconds();
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
                this.simultaneousToEndTime -= timeLearner.getEstimatedTime(this.motherRecipe, nextStep).getStandardSeconds();
                for (Step currStep : this.steps) {
                    if (currStep.isSimultaneous()) {
                        break;
                    }
                    this.simultaneousToEndTime -= timeLearner.getEstimatedTime(this.motherRecipe, currStep).getStandardSeconds();
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

    /**
     * A private helper class used to keep steps associated with
     * their recipes.
     */
    private class ScheduledStep {
        public final Step step;
        public final Recipe motherRecipe;

        public ScheduledStep(@NonNull Step s, @NonNull Recipe r) {
            this.step = s;
            this.motherRecipe = r;
        }
    }
}
