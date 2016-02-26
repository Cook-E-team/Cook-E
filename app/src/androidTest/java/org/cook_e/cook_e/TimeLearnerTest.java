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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TimeLearnerTest {
    private static final Step STEP = new Step(Collections.<String>emptyList(), "a step", new Duration(10000), false);
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
    public void testGetTimeNoLearn() {
        assertEquals(10000, learner.getEstimatedTime(STEP).getMillis());
    }

    @Test
    public void testGetTimeOneLearn() {
        learner.learnStep(STEP, new Duration(8000));
        assertEquals(8000, learner.getEstimatedTime(STEP).getMillis());
    }

    @Test
    public void testClear() {
        learner.clearLearner();
        assertEquals(10000, learner.getEstimatedTime(STEP).getMillis());
    }
}
