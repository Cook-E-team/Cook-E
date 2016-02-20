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

import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/*
 * Unit Tests for the Step class
 */
public class ScheduleUnitTest {
    //  public Step(@NonNull List<String> ingredients, @NonNull String action,
    // @NonNull String description, @NonNull ReadableDuration duration, @NonNull boolean isSimultaneous) {
    // public Recipe(@NonNull String title, @NonNull String author, @NonNull List<Step> steps) {
    public List<String> ingre = new ArrayList<String>();
    public final Step fiveNonSimul = new Step(ingre, "t1", Duration.standardMinutes(5), false);
    public final Step tenNonSimul = new Step(ingre, "t1", Duration.standardMinutes(10), false);
    public final Step sevenNonSimul = new Step(ingre, "t1", Duration.standardMinutes(7), false);

    public final Step fiveSimul = new Step(ingre, "t1", Duration.standardMinutes(5), true);
    public final Step tenSimul = new Step(ingre, "t1", Duration.standardMinutes(10), true);
    public final Step sevenSimul = new Step(ingre, "t1", Duration.standardMinutes(7), true);

    @Test
    public void testSchedule1nonSimul() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        List<Recipe> recipies = new ArrayList<>();
        Recipe recipeN5N5 = new Recipe("N5N7", "test", steps);
        recipies.add(recipeN5N5);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);
        assertEquals(recipeN5N5.getSteps().size(), sched.getStepCount());
        for(int i = 0; i < sched.getStepCount(); i++) {
            assertEquals(recipeN5N5.getSteps().get(i), sched.getNextStep());
        }
    }

    @Test
    public void testSchedule2nonSimul() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        List<Recipe> recipies = new ArrayList<>();
        Recipe recipeN5N7 = new Recipe("N5N5", "test", steps);
        recipies.add(recipeN5N7);
        steps.clear();
        steps.add(tenNonSimul);
        steps.add(tenNonSimul);
        Recipe recipeN10N10 = new Recipe("N10N10", "test", steps);
        recipies.add(recipeN10N10);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);
        assertEquals(4, sched.getStepCount());
        for(int i = 0; i < sched.getStepCount(); i++) {
            assertEquals((i < 2) ? recipeN5N7.getSteps().get(i) :
                                   recipeN10N10.getSteps().get(i),
                                   sched.getNextStep());
        }
    }

    @Test
    public void testSchedule1nonSimul1Simul() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        List<Recipe> recipies = new ArrayList<>();
        Recipe recipeN5N7 = new Recipe("N5N5", "test", steps);
        recipies.add(recipeN5N7);
        steps.clear();
        steps.add(tenSimul);
        steps.add(tenNonSimul);
        Recipe recipe10N10 = new Recipe("10N10", "test", steps);
        recipies.add(recipe10N10);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);
        assertEquals(4, sched.getStepCount());
        steps.clear();
        steps.add(tenSimul);
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        steps.add(tenNonSimul);
        for(int i = 0; i < sched.getStepCount(); i++) {
            assertEquals(steps.get(i),
                    sched.getNextStep());
        }
    }

    @Test
    public void testSchedule2Simul() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        List<Recipe> recipies = new ArrayList<>();
        Recipe recipeN5N7 = new Recipe("N5N5", "test", steps);
        recipies.add(recipeN5N7);
        steps.clear();
        steps.add(tenSimul);
        steps.add(tenNonSimul);
        Recipe recipe10N10 = new Recipe("10N10", "test", steps);
        recipies.add(recipe10N10);
        steps.clear();
        steps.add(fiveNonSimul);
        steps.add(tenSimul);
        Recipe recipeN5Y10 = new Recipe("N5Y10", "test", steps);
        recipies.add(recipe10N10);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);
        assertEquals(6, sched.getStepCount());
        steps.clear();
        steps.add(tenSimul);
        steps.add(fiveNonSimul);
        steps.add(tenSimul);
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        steps.add(tenNonSimul);
        for(int i = 0; i < sched.getStepCount(); i++) {
            assertEquals(steps.get(i),
                    sched.getNextStep());
        }
    }
}
