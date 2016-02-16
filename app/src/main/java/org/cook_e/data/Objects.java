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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * A utility class similar to {@link java.util.Objects} but available in older Android versions
 */
public class Objects {

    /**
     * Throws a {@link NullPointerException} with the specified message if the provided object is
     * null
     * @param o the object to check
     * @param message the message to include in the exception
     * @throws NullPointerException if o is null
     */
    public static void requireNonNull(@Nullable Object o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
    }


    /**
     * Converts an array of type T to an array of type U by casting each item
     * @param in the array to convert
     * @param outClass The class of an array of the output type (should be U[].class)
     * @param <T> the input type
     * @param <U> the output type
     * @return an array of T objects containing the same objects as the input array
     * @throws ArrayStoreException if any element of the input array cannot be cast into U
     */
    @NonNull
    public static <T, U extends T> U[] castArray(@NonNull T[] in, @NonNull Class<? extends U[]> outClass) {
        return Arrays.copyOf(in, in.length, outClass);
    }

    // Prevent construction
    private Objects() {}
}
