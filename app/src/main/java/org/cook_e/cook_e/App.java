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

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.cook_e.data.Bunch;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.cook_e.data.StorageAccessor;
import org.joda.time.Duration;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class that stores application-wide state
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    /**
     * The application context
     */
    private static Context context;
    private static final int DISPLAY_LIMIT = 10;
    /**
     * The storage accessor used to access data
     */
    private static StorageAccessor mAccessor;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        try {
            mAccessor = new StorageAccessor(context);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to connect to database", e);
        }
    }

    /**
     * Returns the application context for this application.
     *
     * This method will only return a non-null value after the application has been created, so
     * it should not be called from static initializers.
     * @return the application context, or null if none is available
     */
    public static Context getAppContext() {
        return context;
    }

    /**
     * Returns the storage accessor used by the application
     * @return the storage accessor
     */
    @NonNull
    public static StorageAccessor getAccessor() {
        if (mAccessor == null) {
            throw new IllegalStateException("Application not yet created");
        }
        return mAccessor;
    }
    public static int getDisplayLimit() {
        return DISPLAY_LIMIT;
    }
}
