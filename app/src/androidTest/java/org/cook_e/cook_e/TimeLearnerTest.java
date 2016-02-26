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

import org.cook_e.data.Step;
import org.cook_e.data.TimeLearner;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TimeLearnerTest {
    private static final Step STEP_ONE = new Step(Collections.<String>emptyList(), "step 1", new Duration(10000), false);
    private static final Step STEP_TWO = new Step(Collections.<String>emptyList(), "step 2", new Duration(20000), false);
    private static final Step STEP_THREE = new Step(Collections.<String>emptyList(), "step 3", new Duration(10000), true);

    private TimeLearner learner;

    @Before
    public void setUp() {
        learner = new TimeLearner();
    }

    @After
    public void tearDown() {
        learner = null;
    }

    @Test
    public void testOneStepNoLearn() {
        assertEquals(10000, learner.getEstimatedTime(STEP_ONE).getMillis());
    }

    @Test
    public void testOneStepOneLearn() {
        learner.learnStep(STEP_ONE, new Duration(8000));
        assertEquals(8000, learner.getEstimatedTime(STEP_ONE).getMillis());
    }

    @Test
    public void testClear() {
        learner.clearLearner();
        assertEquals(10000, learner.getEstimatedTime(STEP_ONE).getMillis());
    }

    @Test
    public void testOneStepOneLearnOverLimit() {
        learner.learnStep(STEP_ONE, new Duration(3000));
        assertEquals(5000, learner.getEstimatedTime(STEP_ONE).getMillis());
        learner.clearLearner();
    }

    @Test
    public void testOneStepMulLearn() {
        learner.learnStep(STEP_ONE, new Duration(8000));
        assertEquals(8000, learner.getEstimatedTime(STEP_ONE).getMillis());
        learner.learnStep(STEP_ONE, new Duration(12000));
        assertEquals(11000, learner.getEstimatedTime(STEP_ONE).getMillis());
        learner.learnStep(STEP_ONE, new Duration(7000));
        assertEquals(8750, learner.getEstimatedTime(STEP_ONE).getMillis());
        learner.clearLearner();
    }

    @Test
    public void testMulStep() {
        learner.learnStep(STEP_ONE, new Duration(8000));
        assertEquals(8000, learner.getEstimatedTime(STEP_ONE).getMillis());
        learner.learnStep(STEP_TWO, new Duration(30000));
        assertEquals(8000, learner.getEstimatedTime(STEP_ONE).getMillis());
        assertEquals(30000, learner.getEstimatedTime(STEP_TWO).getMillis());
        learner.learnStep(STEP_THREE, new Duration(12000));
        assertEquals(8000, learner.getEstimatedTime(STEP_ONE).getMillis());
        assertEquals(30000, learner.getEstimatedTime(STEP_TWO).getMillis());
        assertEquals(12000, learner.getEstimatedTime(STEP_THREE).getMillis());
        learner.learnStep(STEP_ONE, new Duration(12000));
        assertEquals(11000, learner.getEstimatedTime(STEP_ONE).getMillis());
        assertEquals(30000, learner.getEstimatedTime(STEP_TWO).getMillis());
        assertEquals(12000, learner.getEstimatedTime(STEP_THREE).getMillis());
        learner.clearLearner();
    }
}
