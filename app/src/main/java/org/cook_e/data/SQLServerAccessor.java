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

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * This class implements methods for storing and accessing an external sqlserver database
 */
public class SQLServerAccessor implements SQLAccessor {
    /**
     * The tag used for logging
     */
    private static final String TAG = SQLServerAccessor.class.getSimpleName();

    /**
     * The server domain name
     */
    private static final String DOMAIN = "cook-e.database.windows.net";

    /**
     * The server port to connect on
     */
    private static final int PORT = 1433;

    /**
     * The database name
     */
    private static final String DATABASE = "Cook-E";
    /**
     * The database username
     */
    private static final String USERNAME = "ReadOnlyLogin";
    /**
     * The password
     */
    private static final String PASSWORD = "A2305Bmcnsdf";

    private static final String RECIPE_TABLE_NAME = "Recipes";
    private static final String BUNCH_TABLE_NAME = "Bunches";
    private static final String BUNCH_RECIPE_TABLE_NAME = "BunchRecipes";
    /**
     * SQL query that creates the recipe table if it does not exist
     */
    private static final String RECIPE_TABLE_CREATE = String.format(Locale.US, "IF NOT EXISTS " +
            "(SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = N'%s') " +
            "CREATE TABLE %s (id INTEGER PRIMARY KEY, name NVARCHAR(MAX) NOT NULL DEFAULT '', " +
            "author NVARCHAR(MAX) NOT NULL DEFAULT '', description NVARCHAR(MAX) NOT NULL DEFAULT '');",
            RECIPE_TABLE_NAME, RECIPE_TABLE_NAME);
    /**
     * SQL query that creates the recipe-bunch relation table if it does note exist
     */
    private static final String BUNCH_RECIPE_TABLE_CREATE = String.format(Locale.US, "IF NOT EXISTS " +
            "(SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = N'%s') " +
            "CREATE TABLE %s (bunch_id INTEGER NOT NULL, recipe_id INTEGER NOT NULL, " +
            "PRIMARY KEY (bunch_id, recipe_id));",
            BUNCH_RECIPE_TABLE_NAME, BUNCH_RECIPE_TABLE_NAME);

    /**
     * SQL query that creates the bunch table if it does not exist
     */
    private static final String BUNCH_TABLE_CREATE = String.format(Locale.US, "IF NOT EXISTS " +
            "(SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = N'%s') " +
            "CREATE TABLE %s (id INTEGER PRIMARY KEY, name NVARCHAR(MAX) NOT NULL DEFAULT '');",
            BUNCH_TABLE_NAME, BUNCH_TABLE_NAME);

    /**
     * Statement (with placeholders) for inserting a recipe
     */
    private static final String RECIPE_INSERT = "INSERT INTO " + RECIPE_TABLE_NAME +
            " (id, name, author, description) VALUES (?, ?, ?, ?)";

    /**
     * Statement (with placeholders) for selecting a recipe based on its title and author
     */
    private static final String RECIPE_SELECT_TITLE_AUTHOR = "SELECT id, name, author, description" +
            " FROM " + RECIPE_TABLE_NAME + " WHERE name = ? AND author = ?";
    /**
     * Statement (with placeholders) for selecting a recipe based on a name fragment
     */
    private static final String RECIPE_SELECT_LIKE = "SELECT id, name, author, description FROM "
            + RECIPE_TABLE_NAME + " WHERE name LIKE ?";

    /**
     * Parser used for transforming strings to recipes and recipes to strings
     */
    private StorageParser mParser;

    /**
     * The database connection
     */
    private final Connection mConnection;

    /**
     * Prepared statement for inserting a recipe
     */
    private final PreparedStatement mRecipeInsertStatement;
    /**
     * Prepared statement for selecting a recipe by title and author
     */
    private final PreparedStatement mRecipeSelectStatement;
    /**
     * Prepared statement for selecting a recipe based on a name fragment
     */
    private final PreparedStatement mRecipeSelectLikeStatement;

    /**
     * This counter is always one greater than the ID of the last inserted recipe. It is used to
     * assign non-duplicate IDs.
     */
    private long mRecipeCounter;
    /**
     * This counter is always one greater than the ID of the last inserted bunch. It is used to
     * assign non-duplicate IDs.
     */
    private long mBunchCounter;

    /**
     * Constructor
     *
     * @param parser StorageParser that can transform strings to recipes and recipes to strings
     */
    public SQLServerAccessor(StorageParser parser) throws SQLException {
        this.mParser = parser;

        final Properties properties = new Properties();
        properties.put("user", USERNAME);
        properties.put("password", PASSWORD);
        properties.put("encrypt", "true");
        properties.put("trustServerCertificate", "false");
        properties.put("hostNameInCertificate", "*.database.windows.net");
        properties.put("loginTimeout", 30);
        final String dbUrl = "jdbc:jtds:sqlserver://" + DOMAIN + ":" + PORT + "/" + DATABASE;

        // Allow network access on main thread (for testing only)
        // TODO: Remodel all database access to run on a separate thread (issue #26)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());


        // Load driver
        new net.sourceforge.jtds.jdbc.Driver();
        mConnection = DriverManager.getConnection(dbUrl, properties);
        Log.d(TAG, "Successfully connected to remote database");

        verifySchema();
        // Set up prepared statements
        mRecipeInsertStatement = mConnection.prepareStatement(RECIPE_INSERT);
        mRecipeSelectStatement = mConnection.prepareStatement(RECIPE_SELECT_TITLE_AUTHOR);
        mRecipeSelectLikeStatement = mConnection.prepareStatement(RECIPE_SELECT_LIKE);
        // Set up ID counters
        setUpCounters();
    }


    @Override
    public Recipe loadRecipe(String name, String author) throws SQLException {
        mRecipeSelectStatement.setString(1, name);
        mRecipeSelectStatement.setString(2, author);
        mRecipeSelectStatement.execute();
        final ResultSet results = mRecipeSelectStatement.getResultSet();
        try {
            if (results.next()) {
                return recipeFromResult(results);
            }
            else {
                return null;
            }
        } catch (ParseException e) {
            throw new SQLException("Failed to parse recipe steps", e);
        } finally {
            results.close();
        }
    }

    @Override
    public List<Recipe> findRecipesLike(String title) throws SQLException {
        // Create an expression for any string that contains the query
        final String expression = '%' + title + '%';
        mRecipeSelectLikeStatement.setString(1, expression);
        mRecipeSelectLikeStatement.execute();

        final List<Recipe> recipes = new ArrayList<>();
        final ResultSet results = mRecipeSelectLikeStatement.getResultSet();
        try {
            while (results.next()) {
                try {
                    recipes.add(recipeFromResult(results));
                }
                catch (ParseException e) {
                    // TODO: Should this be reported in some other way?
                    // Will proceed to the next recipe
                    Log.w(TAG, "Failed to parse recipe steps");
                }
            }
        }
        finally {
            results.close();
        }
        return recipes;
    }

    /**
     * Creates a Recipe from a ResultSet
     * @param result the ResultSet to read from. This must already be set to a valid row.
     * @return a Recipe
     * @throws SQLException if an error occurs
     * @throws ParseException if the steps could not be parsed
     */
    private Recipe recipeFromResult(ResultSet result) throws SQLException, ParseException {
        final long id = result.getLong("id");
        final String name = result.getString("name");
        final String author = result.getString("author");
        final String description = result.getString("description");
        final List<Step> steps = mParser.parseRecipeSteps(description);

        final Recipe recipe = new Recipe(name, author, steps);
        recipe.setObjectId(id);
        return recipe;
    }

    @Override
    public void checkInvariants() throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void storeRecipe(Recipe r) throws SQLException {
        r.setObjectId(++mRecipeCounter);
        mRecipeInsertStatement.setLong(1, r.getObjectId());
        mRecipeInsertStatement.setString(2, r.getTitle());
        mRecipeInsertStatement.setString(3, r.getAuthor());
        final String description = mParser.serializeRecipeSteps(r.getSteps());
        mRecipeInsertStatement.setString(4, description);

        mRecipeInsertStatement.execute();
    }

    @Override
    public void editRecipe(Recipe r) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Recipe> loadAllRecipes() throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteRecipe(Recipe r) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Bunch loadBunch(String name) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void storeBunch(Bunch b) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void editBunch(Bunch b) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Bunch> loadAllBunches() throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteBunch(Bunch b) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override
    public void storeLearnerData(Recipe r, Collection<LearningWeight> weights) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override
    public Collection<LearningWeight> loadLearnerData(Recipe r) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override
    public void clearAllTables() throws SQLException {
        final Statement statement = mConnection.createStatement();
        try {
            statement.executeUpdate(String.format(Locale.US, "DELETE FROM %s", RECIPE_TABLE_NAME));
            statement.executeUpdate(String.format(Locale.US, "DELETE FROM %s", BUNCH_TABLE_NAME));
            statement.executeUpdate(String.format(Locale.US, "DELETE FROM %s", BUNCH_RECIPE_TABLE_NAME));
        }
        finally {
            statement.close();
        }
    }

    /**
     * Sets up the tables if they do not exist
     */
    private void verifySchema() throws SQLException {
        final Statement statement = mConnection.createStatement();
        try {
            statement.executeUpdate(RECIPE_TABLE_CREATE);
            statement.executeUpdate(BUNCH_TABLE_CREATE);
            statement.executeUpdate(BUNCH_RECIPE_TABLE_CREATE);
        }
        finally {
            statement.close();
        }
    }

    /**
     * Initializes {@link #mRecipeCounter} and {@link #mBunchCounter} to one greater than the
     * greatest ID of any recipe/bunch in the database
     *
     * If an error occurs or no items exist, sets the corresponding counter to 1.
     */
    private void setUpCounters() {
        final Statement statement;
        try {
            statement = mConnection.createStatement();
            try {
                statement.execute("SELECT TOP (1) id FROM " + RECIPE_TABLE_NAME + " ORDER BY id DESC");
                ResultSet results = statement.getResultSet();
                if (results.next()) {
                    mRecipeCounter = results.getLong("id") - 1;
                } else {
                    mRecipeCounter = -1;
                }
                results.close();
                statement.execute(
                        "SELECT TOP (1) id FROM " + BUNCH_TABLE_NAME + " ORDER BY id DESC");
                results = statement.getResultSet();
                if (results.next()) {
                    mBunchCounter = results.getLong("id") - 1;
                } else {
                    mRecipeCounter = -1;
                }
                results.close();
            } finally {
                statement.close();
            }
        } catch (SQLException e) {
            Log.w(TAG, "Failed to get counter values, setting counters to 1");
            mBunchCounter = -1;
            mRecipeCounter = -1;
        }
    }
}
