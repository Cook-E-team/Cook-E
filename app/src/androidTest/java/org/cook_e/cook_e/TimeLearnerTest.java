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
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimeLearnerTest {
    public final List<String> list = new ArrayList<String>();
    public final Step s = new Step(list, "a step", new Duration(10000), false);
    public final TimeLearner learner = new TimeLearner();

    @Test
    public void testGetTimeNoLearn() {
        assertEquals(10000, learner.getEstimatedTime(s));
    }

    @Test
    public void testGetTimeOneLearn() {
        learner.learnStep(s, new Duration(8000));
        assertEquals(8000, learner.getEstimatedTime(s));
        learner.clearLearner();
    }
}
