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
import java.util.Map;
import java.util.HashMap;

public class MemoryAccessor {
    private Map<Pair<String, String>, Recipe> buffer;
    private static final String STORAGE_FILE = "recipe_storage";
    private static final int BUFFER_LIMIT = 5;
    public MemoryAccessor() {
        buffer = new HashMap<Pair<String, String>, Recipe>();
    }
    public void storeRecipe(Recipe r) {

    }
    public void storeBunch(Bunch b) {

    }
    public Recipe loadRecipe(String title, String author) {
        return null;
    }
    public void deleteRecipe(Recipe r) {

    }
    public void deleteRecipe(String title, String author) {

    }
    public void deleteBunch(Bunch b) {

    }
}
