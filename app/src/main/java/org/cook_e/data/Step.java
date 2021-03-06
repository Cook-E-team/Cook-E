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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/*
 * Class representing a step in a recipe
 *
 * Every step has a human-readable description, an action category, list of ingredients, and an
 * estimated time the step takes to perform. None of these values may be null.
 *
 * Objects of this class are immutable.
 */
public final class Step implements Parcelable {

    /**
     * A human-readable mDescription of the actions involved in this step
     */
    @NonNull
    private final String mDescription;

    /**
     * The expected time required to complete this step
     */
    @NonNull
    private final Duration mTime;

    /**
     * The ingredients required for this step
     */
    @NonNull
    private final List<String> mIngredients;

    /**
     * Whether this step can be done simultaneously
     */
    private final boolean mSimultaneous;
    /**
     * index of this step in the recipe
     */
    private final int mIndex;
    /**
     * The set of all string patterns in the description
     * that indicates this step can be done simultaneously
     */
    private static final Set<String> SIMULTANEOUS_PATTERNS = Collections.unmodifiableSet(new HashSet<String>() {{
        add("boil");
        add("bake");
        add("microwave");
    }});
    /**
     * Creates a Step
     * @param ingredients the ingredients required for this step
     * @param description a human-readable description of this step
     * @param duration an estimate of the time required to complete this step
     * @param isSimultaneous if this step can be done simultaneously
     * @throws NullPointerException if any parameter is null
     */
    public Step(@NonNull List<String> ingredients, @NonNull String description,
                @NonNull ReadableDuration duration, boolean isSimultaneous, int index) {
        Objects.requireNonNull(ingredients, "ingredients must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        mDescription = description;
        mTime = duration.toDuration();
        mIngredients = new ArrayList<>(ingredients);
        this.mSimultaneous = isSimultaneous;
        mIndex = index;
    }

    /**
     * Creates a Step without knowing if step can be done simultaneously
     * @param ingredients the ingredients required for this step
     * @param description a human-readable description of this step
     * @param duration an estimate of the time required to complete this step
     * @throws NullPointerException if any parameter is null
     */
    public Step(@NonNull List<String> ingredients, @NonNull String description,
                @NonNull ReadableDuration duration) {
        this(ingredients, description, duration, isSimultaneousParser(description), -1);
    }

    /**
     * Identifies if a step can be done simultaneously
     * @param description description of the step
     * @return true if this step can be done simultaneously, false otherwise
     */
    private static boolean isSimultaneousParser(@NonNull String description) {
        Objects.requireNonNull(description, "description must not be null");
        String[] words = description.split("\\s+");
        for (String word : words) {
            String lowerCaseWord = word.toLowerCase(Locale.US);
            for (String pattern : SIMULTANEOUS_PATTERNS) {
                if (lowerCaseWord.contains(pattern)) return true;
            }
        }
        return false;
    }

    /**
     * Returns the description of this step
     * @return the description
     */
    @NonNull
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the duration of this step
     * @return the duration
     */
    @NonNull
    public ReadableDuration getTime() {
        return mTime;
    }

    public int getIndex() {
        return mIndex;
    }

    /**
     * Returns the duration of this step, truncated to minute precision
     *
     * The behavior of this method is undefined if the number of minutes in the duration of this
     * step is greater than Integer.MAX_VALUE.
     *

     * @return the duration of this step, in minutes
     */
    public int getDurationMinutes() {
        return (int) mTime.getStandardMinutes();
    }

    /**
     * Returns the ingredients that this step requires
     * @return the ingredients
     */
    @NonNull
    public List<String> getIngredients() {
        return new ArrayList<>(mIngredients);
    }

    /**
     * Returns if this step can be done simultaneously
     * @return true if this step can be done simultaneously
     */
    public boolean isSimultaneous() { return mSimultaneous; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        if (mSimultaneous != step.mSimultaneous) return false;
        if (mIndex != step.mIndex) return false;
        if (!mDescription.equals(step.mDescription)) return false;
        if (!mTime.equals(step.mTime)) return false;
        return mIngredients.equals(step.mIngredients);

    }

    @Override
    public int hashCode() {
        int result = mDescription.hashCode();
        result = 31 * result + mTime.hashCode();
        result = 31 * result + mIngredients.hashCode();
        result = 31 * result + (mSimultaneous ? 1 : 0);
        result = 31 * result + mIndex;
        return result;
    }

    @Override
    public String toString() {
        return "Step{" +
                "mDescription='" + mDescription + '\'' +
                ", mTime=" + mTime +
                ", mIngredients=" + mIngredients +
                ", mSimultaneous=" + mSimultaneous +
                ", mIndex=" + mIndex +
                '}';
    }

    // Parceling section

    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {

        @Override
        public Step createFromParcel(Parcel source) {
            final String description = source.readString();
            final Duration duration = (Duration) source.readSerializable();
            final Boolean simultaneous = (Boolean) source.readSerializable();
            final List<String> ingredients = new ArrayList<>();
            source.readStringList(ingredients);
            final int index = source.readInt();

            return new Step(ingredients, description, duration, simultaneous, index);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDescription);
        dest.writeSerializable(mTime);
        dest.writeSerializable(mSimultaneous);
        dest.writeStringList(mIngredients);
        dest.writeInt(mIndex);
    }
}
