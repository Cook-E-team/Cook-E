
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

package org.cook_e.cook_e;

import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.Schedule;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScheduleTest {
    public List<String> ingre = new ArrayList<>();
    public final Step fiveNonSimul = new Step(ingre, "t1", Duration.standardMinutes(5), false);
    public final Step tenNonSimul = new Step(ingre, "t1", Duration.standardMinutes(10), false);
    public final Step sevenNonSimul = new Step(ingre, "t1", Duration.standardMinutes(7), false);

    public final Step fiveSimul = new Step(ingre, "t1", Duration.standardMinutes(5), true);
    public final Step tenSimul = new Step(ingre, "t1", Duration.standardMinutes(10), true);
    public final Step sevenSimul = new Step(ingre, "t1", Duration.standardMinutes(7), true);


    @Test(expected = NullPointerException.class)
    public void testScheduleNull() {
        new Schedule(null);
    }

    @Test
    public void testSchedule1nonSimul() {
        List<Step> steps = new ArrayList<>();
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
        List<Step> steps = new ArrayList<>();
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
                            recipeN10N10.getSteps().get(i - 2),
                    sched.getNextStep());
        }
    }

    @Test
    public void testSchedule1nonSimul1Simul() {
        List<Step> steps = new ArrayList<>();
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
            if (i == 3)
                sched.finishSimultaneousStepFromRecipe(recipe10N10);

            assertEquals(steps.get(i),
                    sched.getNextStep());
        }
    }

    @Test
    public void testSchedule2Simul() {
        List<Step> steps = new ArrayList<>();
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
        recipies.add(recipeN5Y10);
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
            if (i == 4)
                sched.finishSimultaneousStepFromRecipe(recipe10N10);
            else if (i == 5)
                sched.finishSimultaneousStepFromRecipe(recipeN5Y10);

            assertEquals(steps.get(i),
                    sched.getNextStep());
        }
    }

    @Test
    public void testGetCurrStepIndex() {
        List<Step> steps = new ArrayList<>();
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        List<Recipe> recipies = new ArrayList<>();
        Recipe recipeN5N5 = new Recipe("N5N7", "test", steps);
        recipies.add(recipeN5N5);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);

        sched.getNextStep();
        assertEquals(0, sched.getCurrStepIndex());
        sched.getNextStep();
        assertEquals(1, sched.getCurrStepIndex());
        sched.getNextStep();
        assertEquals(1, sched.getCurrStepIndex());
        sched.getPrevStep();
        assertEquals(0, sched.getCurrStepIndex());
        sched.getPrevStep();
        assertEquals(0, sched.getCurrStepIndex());
    }

    @Test
    public void testGetMaxVisitedStepIndex() {
        List<Step> steps = new ArrayList<>();
        steps.add(fiveNonSimul);
        steps.add(sevenNonSimul);
        List<Recipe> recipies = new ArrayList<>();
        Recipe recipeN5N5 = new Recipe("N5N7", "test", steps);
        recipies.add(recipeN5N5);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);

        sched.getNextStep();
        assertEquals(0, sched.getMaxVisitedStepIndex());
        sched.getNextStep();
        assertEquals(1, sched.getMaxVisitedStepIndex());
        sched.getNextStep();
        assertEquals(1, sched.getMaxVisitedStepIndex());
        sched.getPrevStep();
        assertEquals(1, sched.getMaxVisitedStepIndex());
        sched.getPrevStep();
        assertEquals(1, sched.getMaxVisitedStepIndex());
    }

    @Test
    public void testGetCurrentStepRecipe() {
        List<Step> steps1 = new ArrayList<>();
        steps1.add(fiveNonSimul);
        Recipe recipe1 = new Recipe("r1", "test", steps1);
        List<Step> steps2 = new ArrayList<>();
        steps2.add(fiveSimul);
        Recipe recipe2 = new Recipe("r2", "test", steps2);
        List<Recipe> recipies = new ArrayList<>();
        recipies.add(recipe1);
        recipies.add(recipe2);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);

        sched.getNextStep();
        assertEquals(recipe2, sched.getCurrentStepRecipe());
        sched.getNextStep();
        assertEquals(recipe1, sched.getCurrentStepRecipe());
    }

    @Test
    public void isAtFinalStepNoSteps() {
        List<Recipe> recipies = new ArrayList<>();
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);

        assertEquals(true, sched.isAtFinalStep());
        sched.getNextStep();
        assertEquals(true, sched.isAtFinalStep());
    }

    @Test
    public void isAtFinalStep() {
        List<Step> steps1 = new ArrayList<>();
        steps1.add(fiveNonSimul);
        steps1.add(fiveSimul);
        Recipe recipe1 = new Recipe("r1", "test", steps1);
        List<Step> steps2 = new ArrayList<>();
        steps2.add(fiveSimul);
        Recipe recipe2 = new Recipe("r2", "test", steps2);
        List<Recipe> recipies = new ArrayList<>();
        recipies.add(recipe1);
        recipies.add(recipe2);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);

        assertEquals(false, sched.isAtFinalStep());
        sched.getNextStep();
        assertEquals(false, sched.isAtFinalStep());
        sched.getNextStep();
        assertEquals(false, sched.isAtFinalStep());
        sched.getNextStep();
        assertEquals(true, sched.isAtFinalStep());
        sched.getPrevStep();
        assertEquals(false, sched.isAtFinalStep());
    }

    @Test
    public void testGetStepCount() {
        List<Step> steps1 = new ArrayList<>();
        steps1.add(fiveNonSimul);
        steps1.add(fiveSimul);
        Recipe recipe1 = new Recipe("r1", "test", steps1);
        List<Step> steps2 = new ArrayList<>();
        steps2.add(fiveSimul);
        Recipe recipe2 = new Recipe("r2", "test", steps2);
        List<Recipe> recipies = new ArrayList<>();
        recipies.add(recipe1);
        recipies.add(recipe2);
        Bunch bunch = new Bunch("test", recipies);
        Schedule sched = new Schedule(bunch);

        assertEquals(3, sched.getStepCount());
    }
}
