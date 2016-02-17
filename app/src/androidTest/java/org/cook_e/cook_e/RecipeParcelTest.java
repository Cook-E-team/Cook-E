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

package org.cook_e.cook_e;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;

import org.cook_e.data.Ingredient;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests parceling and unparceling of Recipes
 */
public class RecipeParcelTest {


    /**
     * The application context used to access resources
     */
    private Context context;

    @Before
    public void setUp() {
        // Get the application context
        context = InstrumentationRegistry.getTargetContext();
        assertNotNull(context);
    }

    @Test
    public void testNewArray() {
        final int size = 32;
        final Recipe[] array = Recipe.CREATOR.newArray(size);
        assertNotNull(array);
        assertEquals("Incorrect array length", size, array.length);
    }

    @Test
    public void testParcelBasic() {
        final Parcel parcel = Parcel.obtain();
        final Recipe original = new Recipe("Recipe title", "Clamify Flumingaster", new ArrayList<Step>());
        original.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_image_1));

        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        final Recipe unparceled = Recipe.CREATOR.createFromParcel(parcel);
        assertNotNull(unparceled);
        assertEquals(original, unparceled);
    }

    @Test
    public void testParcelOneStep() {
        final Parcel parcel = Parcel.obtain();
        final Ingredient scallops = new Ingredient("Scallops", 4, "");
        final Step step = new Step(Collections.singletonList(scallops), "Poach", "Gently poach the scallops", Duration.standardMinutes(3));
        final Recipe original = new Recipe("Recipe title", "Clamify Flumingaster", Collections.singletonList(step));
        original.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_image_1));

        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        final Recipe unparceled = Recipe.CREATOR.createFromParcel(parcel);

        assertNotNull(unparceled);
        assertEquals(original, unparceled);

        final Ingredient unparceledIngredient = unparceled.getSteps().get(0).getIngredients().get(0);
        assertEquals(scallops, unparceledIngredient);
    }

    @Test
    public void testParcelTwoSteps() {
        final Parcel parcel = Parcel.obtain();
        final Ingredient scallops = new Ingredient("Scallops", 4, "");
        final Ingredient butter = new Ingredient("Butter", 1, "kilogram");
        final Step step1 = new Step(Collections.singletonList(scallops),
                "Poach", "Gently poach the scallops", Duration.standardMinutes(2));
        final Step step2 = new Step(Collections.singletonList(butter),
                "Melt", "Melt the butter", Duration.standardMinutes(10));
        final Recipe original = new Recipe("Recipe title", "Clamify Flumingaster", Arrays.asList(step1, step2));
        original.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_image_1));

        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        final Recipe unparceled = Recipe.CREATOR.createFromParcel(parcel);

        assertNotNull(unparceled);
        assertEquals(original, unparceled);
    }
}
