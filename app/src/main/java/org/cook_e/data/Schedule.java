package org.cook_e.data;

import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that produces and manages a schedule for a Bunch.
 */
public class Schedule {
    private final List<Recipe> recipes;
    private List<Step> steps;

    /**
     * Creates a schedule based on the given Bunch.
     *
     * @param b the Bunch to schedule steps from
     */
    public Schedule(Bunch b) {
        this.recipes = b.getRecipes();
        this.steps = new ArrayList<Step>();
        for (Recipe r: recipes) {
            steps.addAll(r.getSteps());
        }
    }

    /**
     * Returns the total number steps scheduled. This value
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
}
