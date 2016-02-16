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

/**
 * Created by kylewoo on 2/16/16.
 */
public class StorageAccessor {
    private MemoryAccessor memory;
    private DatabaseAccessor database;
    private StorageParser parser;
    public StorageAccessor() {
        memory = new MemoryAccessor();
        database = new DatabaseAccessor();
        parser = new StorageParser();
    }
    public void storeRecipe(Recipe r) {
        memory.storeRecipe(r);
    }
    public void storeBunch(Bunch b) {
        memory.storeBunch(b);
    }
    public Recipe loadRecipe(String title, String author) {
        Recipe r = null;
        r = memory.loadRecipe(title, author);
        if (r == null) {
            r = database.findRecipe(title, author); // this section will be expanded when database is implemented
        }
        return r;
    }
    public void deleteRecipe(String title, String author) {
        memory.deleteRecipe(title,author);
    }
    public void deleteRecipe(Recipe r) {
        memory.deleteRecipe(r);
    }
    public void deleteBunch(Bunch b) {
        memory.deleteBunch(b);
    }
}
