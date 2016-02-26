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
import org.cook_e.data.CookingTimeEstimator;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tyler on 2/25/2016.
 */
public class CookingTimeEstimatorTest {
    private List<String> sampleIngredients = new ArrayList<String>();
    private String sampleDescription = "";
    private ReadableDuration oneMinuteDuration =  Duration.standardMinutes(1);
    private ReadableDuration twoMinuteDuration =  Duration.standardMinutes(2);



    @Test
    public void testOriginalNoRecipes() {
        Bunch bunch = new Bunch();
        int originalTime = CookingTimeEstimator.getOriginalTime(bunch);
        assertEquals(0, originalTime);
    }

    @Test
    public void testOriginalNoSteps() {
        Recipe recipe = new Recipe("title", "author", new ArrayList<Step>());
        Bunch bunch = new Bunch();
        int originalTime = CookingTimeEstimator.getOriginalTime(bunch);
        assertEquals(0, originalTime);
    }

    @Test
    public void testOriginalNonsimultaneousStep() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(new Step(sampleIngredients, sampleDescription, oneMinuteDuration, false));
        Recipe recipe = new Recipe("title", "author", steps);
        Bunch bunch = new Bunch();
        bunch.addRecipe(recipe);
        int originalTime = CookingTimeEstimator.getOriginalTime(bunch);
        assertEquals(1, originalTime);
    }

    @Test
    public void testOriginalSimultaneousStep() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(new Step(sampleIngredients, sampleDescription, oneMinuteDuration, true));
        Recipe recipe = new Recipe("title", "author", steps);
        Bunch bunch = new Bunch();
        bunch.addRecipe(recipe);
        int originalTime = CookingTimeEstimator.getOriginalTime(bunch);
        assertEquals(1, originalTime);
    }

    @Test
    public void testOriginalMultipleSteps() {
        List<Step> steps = new ArrayList<Step>();
        steps.add(new Step(sampleIngredients, sampleDescription, oneMinuteDuration, false));
        steps.add(new Step(sampleIngredients, sampleDescription, oneMinuteDuration, true));
        steps.add(new Step(sampleIngredients, sampleDescription, twoMinuteDuration, false));
        Recipe recipe = new Recipe("title", "author", steps);
        Bunch bunch = new Bunch();
        bunch.addRecipe(recipe);
        int originalTime = CookingTimeEstimator.getOriginalTime(bunch);
        assertEquals(4, originalTime);
    }

    @Test
    public void testOriginalMultipleRecipes() {
        List<Step> steps1 = new ArrayList<Step>();
        steps1.add(new Step(sampleIngredients, sampleDescription, oneMinuteDuration, false));
        Recipe recipe1 = new Recipe("title", "author", steps1);
        List<Step> steps2 = new ArrayList<Step>();
        steps2.add(new Step(sampleIngredients, sampleDescription, oneMinuteDuration, false));
        Recipe recipe2 = new Recipe("title", "author", steps1);
        Bunch bunch = new Bunch();
        bunch.addRecipe(recipe1);
        bunch.addRecipe(recipe2);
        int originalTime = CookingTimeEstimator.getOriginalTime(bunch);
        assertEquals(2, originalTime);
    }

}
