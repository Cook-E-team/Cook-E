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

import android.support.annotation.NonNull;

import org.joda.time.Duration;

import java.sql.SQLException;

/**
 * Created by tylerjacoby on 2/26/16.
 */
public interface TimeLearnerInterface {
    void learnStep(@NonNull Recipe r, Step s, @NonNull Duration time) throws IllegalArgumentException, SQLException;

    @NonNull
    Duration getEstimatedTime(@NonNull Recipe r, @NonNull Step s);
}
