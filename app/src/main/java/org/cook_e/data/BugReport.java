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

import org.joda.time.DateTime;

/**
 * Stores a bug reported by the user
 */
public class BugReport {
    private long id;
    private final String desc;
    private final DateTime date;
    private final String meta;
    public BugReport(String desc, DateTime date, String meta) {
        this.desc = desc;
        this.date = date;
        this.meta = meta;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public DateTime getDate() {
        return date;
    }

    public String getMeta() {
        return meta;
    }
}
