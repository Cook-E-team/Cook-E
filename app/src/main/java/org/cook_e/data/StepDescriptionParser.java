/*
 * Copyright 2016 the Cook-E development team
 *
 *  This file is part of Cook-E.
 *
 *  Cook-E is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Cook-E is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A static class for parsing out information about descriptions.
 */
public final class StepDescriptionParser {
    private StepDescriptionParser(){}

    /**
     * Returns whether or not the given steps description can
     * be done at the same time as other steps.
     *
     * @param description the steps description
     * @return true if the given step description can be done
     * at the same time as other steps, false otherwise
     */
    public static boolean isSimultaneous(String description) {

        ////////// TEMPORARY simple solution //////////
        description = description.toLowerCase();

        Set<String> simulAction = new HashSet<>(Arrays.asList("bake", "boil"));

        return description.contains("boil") || description.contains("bake");
    }

    /**
     * Returns the time in seconds that the step with the given
     * description should take.
     *
     * @param description the steps description
     * @return the time the given step description should take
     * in seconds or -1 if no time could be determined
     */
    public static int getTime(String description) {

        ////////// TEMPORARY dumb solution //////////
        return 5*60;
    }
}
