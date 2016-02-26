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
import org.joda.time.ReadableDuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by kylewoo on 2/25/16.
 */

/*
 * This class creates a SQL file that can be used to put recipes onto the remote database
 * Currently I do not have an efficient way of taking text and making a recipe
 */
public class SQLFileCreator {
    private static final String INSERT_START = "INSERT INTO Recipes" +
            " (id, name, author, description) VALUES (";
    private static final String INSERT_END = ");\n";
    public static void main(String[] args) {
        try {

            FileOutputStream fos = new FileOutputStream("SQL_Recipe_Commands.sql");
            OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
            PrintWriter writer = new PrintWriter(out);
            Recipe r = createFriedRiceRecipe();
            StorageParser parser = new StorageParser();
            String desc = parser.serializeRecipeSteps(r.getSteps());
            r.setObjectId(r.hashCode());
            writer.write(INSERT_START);
            String id = String.valueOf(r.getObjectId());
            writer.write(id);
            writer.write(r.getTitle());
            writer.write(r.getAuthor());
            writer.write(desc);
            writer.write(INSERT_END);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Recipe createFriedRiceRecipe() {
        List<Step> steps = new ArrayList<>();
        List<String> first_ings = new ArrayList<>();
        first_ings.add("4 cups rice");
        Step first = new Step(first_ings, "Cook rice", Duration.standardMinutes(30), true);
        steps.add(first);
        List<String> second_ings = new ArrayList<>();
        first_ings.add("1 carrot");
        Step second = new Step(first_ings, "Shred carrot", Duration.standardMinutes(1), false);
        steps.add(second);
        List<String> third_ings = new ArrayList<>();
        third_ings.add("2 beaten eggs");
        Step third = new Step(third_ings, "Heat a large skillet on medium-high heat. Spray skillet with cooking spray. Scramble eggs in skillet. Remove from pan and keep warm", Duration.standardSeconds(60), false);
        steps.add(third);
        List<String> fourth_ings = new ArrayList<>();
        fourth_ings.add("3-4 slices chopped cooked ham");
        Step fourth = new Step(fourth_ings, "Heat chopped ham in skillet until slightly brown. Remove from the pan and keep warm.", Duration.standardMinutes(2), false);
        steps.add(fourth);
        List<String> fifth_ings = new ArrayList<>();
        fifth_ings.add("1 cup frozen peas");
        fifth_ings.add("carrots");
        fifth_ings.add("rice");
        fifth_ings.add("ham");
        fifth_ings.add("salt");
        fifth_ings.add("pepper");
        Step fifth = new Step(fifth_ings, "Add peas and carrots to skillet and cook until they are tender. Add rice, cooked eggs and ham to the skillet and mix well", Duration.standardMinutes(5), false);
        steps.add(fifth);
        return new Recipe("Fried Rice", "ventra", steps);

    }
}
