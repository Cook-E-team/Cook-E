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

import java.util.ArrayList;
import java.util.List;
import org.joda.time.Duration;


/**
 * This class represents a parser that allows for transforming recipes to strings and strings to recipes
 */
public class StorageParser {
    public StorageParser() {

    }

    /**
     * Method for converting a description text blob into a recipe
     * @param title String title of the recipe
     * @param author String author of the recipe
     * @param description String blob of steps
     * @return Recipe object containing the fields above
     */
    public Recipe convertStringToRecipe(String title, String author, String description) {
        List<Step> newSteps = new ArrayList<Step>();
        String[] strArray = description.split("\n");
        for (String step: strArray) {
            int trailIndex = step.indexOf("mDescription='") + "mDescription='".length();
            int forwardIndex = step.indexOf("', mTime=");
            String mDescription = step.substring(trailIndex, forwardIndex);
            trailIndex = forwardIndex + "', mTime=".length();
            forwardIndex = step.indexOf(", mIngredients=");
            Duration time = Duration.standardMinutes(Integer.valueOf(step.substring(trailIndex, forwardIndex)));
            trailIndex = forwardIndex + ", mIngredients=".length();
            forwardIndex = step.indexOf(", mSimultaneous=");
            List<String> ingredients = toArrayList(step.substring(trailIndex, forwardIndex));
            trailIndex = forwardIndex + ", mSimultaneous=".length();
            forwardIndex = step.indexOf("}");
            boolean simul = step.substring(trailIndex, forwardIndex).equals("true");
            newSteps.add(new Step(ingredients, mDescription, time, simul));
        }
        return new Recipe(title, author, newSteps);
    }

    public static List<String> toArrayList(String str) {
        String[] arr = str.split(",");
        List<String> lst = new ArrayList<String>();
        for (String curr: arr) {
            lst.add(curr);
        }
        return lst;
    }
    /**
     * Method for taking a recipe and generating a description text blob of its steps
     * @param r Recipe to convert
     * @return String description blob of steps
     */
    public String convertRecipeToString(Recipe r) {
        String ans = "";
        for (Step step: r.getSteps()) {
            ans += step.toString() + "\n";
        }
        return ans;
    }
}
