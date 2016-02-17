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

import android.content.Context;

/**
 * Created by kylewoo on 2/16/16.
 */
public class StorageAccessor {
    private SQLiteAccessor sqlite;
    private SQLServerAccessor sqlserver;
    private StorageParser parser;
    public StorageAccessor(Context c) {
        sqlite = new SQLiteAccessor(c, parser);
        sqlserver = new SQLServerAccessor(parser);
        parser = new StorageParser();
    }
    public void storeRecipe(Recipe r) {
        sqlite.storeRecipe(r);
    }
    public void storeBunch(Bunch b) {
        sqlite.storeBunch(b);
    }
    public Recipe loadRecipe(String title, String author) {
        Recipe r = null;
        r = sqlite.loadRecipe(title, author);
        if (r == null) {
            r = sqlserver.findRecipe(title, author); // this section will be expanded when database is implemented
            if (r != null) sqlite.storeRecipe(r);
        }
        return r;
    }
    public void deleteRecipe(String title, String author) {
        sqlite.deleteRecipe(title,author);
    }
    public void deleteRecipe(Recipe r) {
        sqlite.deleteRecipe(r);
    }
    public void deleteBunch(Bunch b) {
        sqlite.deleteBunch(b);
    }
}
