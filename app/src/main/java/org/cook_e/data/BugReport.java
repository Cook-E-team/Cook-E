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

    /**
     * Set the ID for the bug report
     * @param id id for the bug report
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * get the id of the current bug report
     * @return the id for the current bug report
     */
    public long getId() {
        return id;
    }

    /**
     * Description of the bug report
     * @return the description of the bug report
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Date of the report
     * @return date of the report submitted
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * Get meta data from the report
     * @return the meta data of the report
     */
    public String getMeta() {
        return meta;
    }
}
