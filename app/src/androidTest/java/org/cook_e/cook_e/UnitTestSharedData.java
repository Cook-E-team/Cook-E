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
/*
 * This class contains constants and other data for use in unit tests
 */
public class UnitTestSharedData {
    public static class WordPair {
        public final String word;
        public final String plural;

        public WordPair(String word, String plural) {
            this.word = word;
            this.plural = plural;
        }
    }
    public static final String[] ACTIONS = {
            "BOIL",
            "BAKE",
            "FRY",
            "Stir"
    };
    public static final WordPair[] COMMON_UNITS = {
            // SI mass
            new WordPair("microgram", "micrograms"),
            new WordPair("milligram", "milligrams"),
            new WordPair("gram", "grams"),
            new WordPair("kilogram", "kilograms"),
            new WordPair("megagram", "megagrams"),

            // SI volume, United States spelling (liter)
            new WordPair("microliter", "microliters"),
            new WordPair("milliliter", "milliliters"),
            new WordPair("liter", "liters"),
            new WordPair("kiloliter", "kiloliters"),
            // SI volume, other countries spelling (litre)
            new WordPair("microlitre", "microlitres"),
            new WordPair("millilitre", "millilitres"),
            new WordPair("litre", "litres"),
            new WordPair("kilolitre", "kilolitres"),

            // USC mass/force
            new WordPair("grain", "grains"),
            new WordPair("ounce", "ounces"),
            new WordPair("pound", "pounds"),
            new WordPair("stone", "stones"),
            new WordPair("slug", "slugs"),

            // USC liquid volume
            new WordPair("fluid ounce", "fluid ounces"),
            new WordPair("pint", "pints"),
            new WordPair("quart", "quarts"),
            new WordPair("gallon", "gallons"),

            // USC dry volume
            new WordPair("hogshead", "hogsheads"),
            new WordPair("barrel", "barrels"),
            new WordPair("bushel", "bushels"),
            new WordPair("peck", "pecks"),

            // Cooking measures
            new WordPair("teaspoon", "teaspoons"),
            new WordPair("tablespoon", "tablespoons"),
            new WordPair("cup", "cups"),
            new WordPair("desertspoon", "desertspoons"),
            new WordPair("drop", "drops"),
            new WordPair("jigger", "jiggers"),
            new WordPair("gill", "gills"),
            new WordPair("fifth", "fifths"),
    };

    public static final String[] INGREDIENTS = {
            "egg",
            "milk",
            "flour",
            "butter",
            "semisweet chocolate",
            "unsweetened chocolate",
    };
    public static String generateDescription(String ing, String action) {
        return action + " " + ing;
    }
}
