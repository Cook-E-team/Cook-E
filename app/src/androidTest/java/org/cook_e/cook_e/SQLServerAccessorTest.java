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

package org.cook_e.cook_e;

import org.cook_e.data.SQLServerAccessor;
import org.cook_e.data.StorageParser;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;


public class SQLServerAccessorTest {

    private SQLServerAccessor mAccessor;

    @Before
    public void setup() throws SQLException {
        StorageParser parser = new StorageParser();
        mAccessor = new SQLServerAccessor(parser);
    }

    @After
    public void teardown() throws SQLException {
        mAccessor = null;

    }

}


