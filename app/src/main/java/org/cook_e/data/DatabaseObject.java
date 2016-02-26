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

/**
 * A base class for objects that can be stored in a database
 *
 * Provides access to an object ID
 */
public abstract class DatabaseObject {

    /**
     * A special ID value that indicates this object has no ID
     */
    public static final long NO_ID = -1l;

    /**
     * The ID of this object, as used in the database as a row ID
     *
     * A value of {@link #NO_ID} indicates that this object has no ID
     */
    private long mObjectId;

    /**
     * Creates a database object with no ID
     */
    public DatabaseObject() {
        mObjectId = NO_ID;
    }

    /**
     * Sets the ID of this object. The ID to set may be {@link #NO_ID}.
     * @param newId the ID to set
     */
    public final void setObjectId(long newId) {
        mObjectId = newId;
    }

    /**
     * Returns the ID of this object
     * @return the ID of this object, or {@link #NO_ID} if this object has no ID
     */
    public final long getObjectId() {
        return mObjectId;
    }

    /**
     * Determines if this object has a valid ID
     * @return true if this object has a valid ID, otherwise false
     */
    public final boolean hasObjectId() {
        return mObjectId != NO_ID;
    }

}
