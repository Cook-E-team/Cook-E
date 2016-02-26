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

import java.sql.*;

/**
 * Created by kylewoo on 2/26/16.
 */
public class SQLBugUploader {
    /**
     * The tag used for logging
     */
    private static final String TAG = SQLBugUploader.class.getSimpleName();

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
    private static final String DATABASE = "Cook-E-Bugs";
    /**
     * The database username
     */
    private static final String USERNAME = "ReadOnlyLogin";
    /**
     * The password
     */
    private static final String PASSWORD = "A2305Bmcnsdf";

    private static final String BUG_TABLE_NAME = "Bugs";

    private long mBugCounter;

    private static final String INSERT_STATEMENT = "INSERT INTO " + BUG_TABLE_NAME + " (id, desc, date, meta) " +
            "VALUES (?, ?, ?, ?);";

    private final PreparedStatement mInsertStatement;
    /**
     * The database connection
     */
    private final Connection mConnection;

    public SQLBugUploader() throws SQLException {
        mConnection = DriverManager.getConnection("");
        setUpCounters();
        mInsertStatement = mConnection.prepareStatement(INSERT_STATEMENT);
        //:TODO add user to Cook-E-Bugs database
    }
    public void submitBug(BugReport bug) throws SQLException {
        bug.setId(++mBugCounter);
        mInsertStatement.setLong(1, bug.getId());
        mInsertStatement.setString(2, bug.getDesc());
        mInsertStatement.setDate(3, bug.getDate());
        mInsertStatement.setString(4, bug.getMeta());
        mInsertStatement.execute();
    }
    /**
     * Initializes {@link #mBugCounter} to one greater than the
     * greatest ID of any recipe in the database. If the recipes table is empty, sets mRecipeCounter
     * to 1.
     */
    private void setUpCounters() throws SQLException {
        final Statement statement = mConnection.createStatement();
        try {
            statement.execute("SELECT TOP (1) id FROM " + BUG_TABLE_NAME + " ORDER BY id DESC");
            ResultSet results = statement.getResultSet();
            if (results.next()) {
                mBugCounter = results.getLong("id") + 1;
            } else {
                mBugCounter = 1;
            }
            results.close();
        } finally {
            statement.close();
        }
    }


}
