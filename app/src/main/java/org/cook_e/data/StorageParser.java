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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class represents a parser that allows for transforming recipes to strings and strings to recipes
 */
public class StorageParser {

    /**
     * Parses a serialized list of steps
     * @param stepsString a string representing a list of steps, in the format returned by
     *                    {@link #serializeRecipeSteps(List)}
     * @return the steps in the provided String
     * @throws java.text.ParseException if the steps could not be parsed
     */
    public List<Step> parseRecipeSteps(String stepsString) throws ParseException {
        try {
            final JSONArray json = new JSONArray(stepsString);
            final List<Step> steps = new ArrayList<>(json.length());
            for (int i = 0; i < json.length(); i++) {
                final JSONObject stepJson = json.getJSONObject(i);

                final String description = stepJson.getString("description");
                final Duration duration = Duration.millis(stepJson.getLong("duration_ms"));
                final boolean simultaneous = stepJson.getBoolean("simultaneous");

                final JSONArray ingredientsJson = stepJson.getJSONArray("ingredients");
                final List<String> ingredients = new ArrayList<>(ingredientsJson.length());
                for (int j = 0; j < ingredientsJson.length(); j++) {
                    ingredients.add(ingredientsJson.getString(j));
                }
                steps.add(new Step(ingredients, description, duration, simultaneous));
            }

            return steps;
        } catch (JSONException e) {
            final ParseException parseException = new ParseException("Invalid JSON", 0);
            parseException.initCause(e);
            throw parseException;
        }
    }

    /**
     * Serializes a list of steps into a String in an implementation-defined format
     * @param steps the steps to process. Must not be null. Must not contain any null elements.
     * @return a String representation of the steps
     */
    public String serializeRecipeSteps(List<? extends Step> steps) {
        Objects.requireNonNull(steps, "steps must not be null");
        try {
             JSONArray json = new JSONArray();

            for (Step step : steps) {
                final JSONObject stepJson = new JSONObject();
                stepJson.put("description", step.getDescription());
                stepJson.put("duration_ms", step.getTime().getMillis());

                final JSONArray ingredientsJson = new JSONArray(step.getIngredients());
                stepJson.put("ingredients", ingredientsJson);

                stepJson.put("simultaneous", step.isSimultaneous());

                json.put(stepJson);
            }

            // Convert to a string with no indentation
            return json.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Unexpected exception serializing JSON", e);
        }
    }
}
