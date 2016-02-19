package org.cook_e.cook_e;

/**
 * Created by Shan Yaang on 2/17/2016.
 */
import android.os.Parcel;

import org.cook_e.data.Bunch;
import org.cook_e.data.Ingredient;
import org.cook_e.data.Recipe;
import org.cook_e.data.Schedule;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScheduleTest {
    final String scallops = "Scallops";

    @Test
    public void testTwoNonSimulRecipeScheduler() {
        final Step step0 = new Step(Collections.singletonList(scallops), "Gently poach the scallops", Duration.standardMinutes(3), false);
        final Step step1 = new Step(Collections.singletonList(scallops), "Aggresively poach the scallops", Duration.standardMinutes(3), false);
        List<Step> testSteps = new ArrayList<>();
        testSteps.add(step0);
        testSteps.add(step1);
        Recipe original = new Recipe("Recipe title", "Clamify Flumingaster", testSteps);
        List<Recipe> testList = new ArrayList<>();
        testList.add(original);
        testList.add(original);
        Bunch testBunch = new Bunch("test me", testList);
        Schedule schedule = new Schedule(testBunch);
        assertEquals(schedule.getStepCount(), 4);
        for(int i = 0; i < 4; i++) {
            assertEquals((i % 2 == 0) ? step0 : step1, schedule.getNextStep());
        }
    }

    @Test
    public void test1NonSimul1SimulRecipeScheduler() {
        final Step step0 = new Step(Collections.singletonList(scallops), "Gently poach the scallops", Duration.standardMinutes(3), true);
        final Step step1 = new Step(Collections.singletonList(scallops), "Aggresively poach the scallops", Duration.standardMinutes(3), false);
        final Step step2 = new Step(Collections.singletonList(scallops), "Aggresively bake the scallops", Duration.standardMinutes(3), true);
        List<Step> testSteps0 = new ArrayList<>();
        testSteps0.add(step1);
        testSteps0.add(step1);
        List<Step> testSteps1 = new ArrayList<>();
        testSteps1.add(step0);
        testSteps1.add(step2);
        Recipe original0 = new Recipe("r0", "Clamify Flumingaster", testSteps0);
        Recipe original1 = new Recipe("r1", "Clamify Flumingaster", testSteps1);
        List<Recipe> testList = new ArrayList<>();
        testList.add(original0);
        testList.add(original1);
        Bunch testBunch = new Bunch("test me", testList);
        Schedule schedule = new Schedule(testBunch);
        assertEquals(schedule.getStepCount(), 4);
        assertEquals(step0, schedule.getNextStep());
        assertEquals(step2, schedule.getNextStep());
        assertEquals(step1, schedule.getNextStep());
        assertEquals(step1, schedule.getNextStep());
    }

    @Test
    public void test1NonSimul2SimulRecipeScheduler() {
        final Step step0 = new Step(Collections.singletonList(scallops), "Gently poach the scallops", Duration.standardMinutes(3), false);
        final Step step1 = new Step(Collections.singletonList(scallops), "Aggresively poach the scallops", Duration.standardMinutes(3), true);
        final Step step2 = new Step(Collections.singletonList(scallops), "Aggresively bake the scallops", Duration.standardMinutes(3), false);
        final Step step3 = new Step(Collections.singletonList(scallops), "Aggresively smash the scallops", Duration.standardMinutes(3), true);
        final Step step4 = new Step(Collections.singletonList(scallops), "Aggresively boil the scallops", Duration.standardMinutes(3), false);
        List<Step> testSteps0 = new ArrayList<Step>();
        testSteps0.add(step1);
        testSteps0.add(step1);
        List<Step> testSteps1 = new ArrayList<Step>();
        testSteps1.add(step0);
        testSteps1.add(step2);
        List<Step> testSteps2 = new ArrayList<Step>();
        testSteps2.add(step3);
        testSteps2.add(step4);
        testSteps2.add(step3);
        Recipe original0 = new Recipe("r0", "Clamify Flumingaster", testSteps0);
        Recipe original1 = new Recipe("r1", "Clamify Flumingaster", testSteps1);
        Recipe original2 = new Recipe("r2", "dope", testSteps2);
        List<Recipe> testList = new ArrayList<Recipe>();
        testList.add(original0);
        testList.add(original1);
        testList.add(original2);
        Bunch testBunch = new Bunch("test me", testList);
        Schedule schedule = new Schedule(testBunch);
        assertEquals(schedule.getStepCount(), 7);
        assertEquals(step3, schedule.getNextStep());
        assertEquals(step4, schedule.getNextStep());
        assertEquals(step0, schedule.getNextStep());
        assertEquals(step2, schedule.getNextStep());
        assertEquals(step3, schedule.getNextStep());
        assertEquals(step1, schedule.getNextStep());
        assertEquals(step1, schedule.getNextStep());
    }
}
