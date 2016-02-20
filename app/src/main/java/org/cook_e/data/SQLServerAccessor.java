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
import android.provider.ContactsContract;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class implements methods for storing and accessing an external sqlserver database
 */
public class SQLServerAccessor implements SQLAccessor {
    /**
     * Connection token that will be used to setup a connection with the sqlserver database
     */
    private static final String CONNECTION_TOKEN =
            "jdbc:sqlserver://cook-e.database.windows.net:1433" +
            ";database=Cook-E;" +
            "user=cook-e-admin@cook-e;" +
            "password=Developer1;" +
            "encrypt=true;" +
            "trustServerCertificate=false;" +
            "hostNameInCertificate=*.database.windows.net;" +
            "loginTimeout=30;";
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


    @Override
    public Recipe loadRecipe(String name, String author) throws SQLException {
        return null;
    }
    @Override
    public List<Recipe> findRecipesLike(String description) throws SQLException {
        return null;
    }
    @Override
    public void storeRecipe(Recipe r) throws SQLException {}
    @Override
    public void editRecipe(Recipe r) throws SQLException {}
    @Override
    public List<Recipe> loadAllRecipes() throws SQLException { return null; }
    @Override
    public void deleteRecipe(Recipe r) throws SQLException {}
    @Override
    public Bunch loadBunch(String name) throws SQLException { return null; }
    @Override
    public void storeBunch(Bunch b) throws SQLException { }
    @Override
    public void editBunch(Bunch b) throws SQLException { }
    @Override
    public List<Bunch> loadAllBunches() throws SQLException { return null; }
    @Override
    public void deleteBunch(Bunch b) throws SQLException { }

    public void clearAllTables() {}
}
