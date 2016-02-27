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

import org.cook_e.data.AsyncAccessor;
import org.cook_e.data.StorageAccessor;

import java.sql.SQLException;

/**
 * A class that stores application-wide state
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    /**
     * The application context
     */
    private static Context context;

    /**
     * The storage accessor used to access data
     */
    private static StorageAccessor mAccessor;

    /**
     * The asynchronous accessor used to access data
     */
    private static AsyncAccessor mAsyncAccessor;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
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
            if (context == null) {
                throw new IllegalStateException("Application does not exist");
            }
            try {
                mAccessor = new StorageAccessor(context);
            } catch (SQLException e) {
                Log.e(TAG, "Failed to connect to database", e);
            }
        }
        return mAccessor;
    }

    @NonNull
    public static AsyncAccessor getAsyncAccessor() {
        if (mAsyncAccessor == null) {
            mAsyncAccessor = new AsyncAccessor(getAccessor());
        }
        return mAsyncAccessor;
    }
}
