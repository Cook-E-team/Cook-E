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

import android.provider.ContactsContract;
import java.util.List;
import java.util.ArrayList;
/**
 * This class implements methods for storing and accessing an external sqlserver database
 */
public class SQLServerAccessor {
    /**
     * Connection token that will be used to setup a connection with the sqlserver database
     */
    private static final String CONNECTION_TOKEN = "";
    /**
     * Parser used for transforming strings to recipes and recipes to strings
     */
    private StorageParser parser;

    /**
     * Constructor
     * @param parser StorageParser that can transform strings to recipes and recipes to strings
     */
    public SQLServerAccessor(StorageParser parser) {
        this.parser = parser;
    }

    /**
     * Search for a recipe
     * @param title
     * @param author
     * @return
     */
    public Recipe findRecipe(String title, String author) {
        return null;
    }
    public List<Recipe> findRecipeLike(String description) {
        return null;
    }
}
