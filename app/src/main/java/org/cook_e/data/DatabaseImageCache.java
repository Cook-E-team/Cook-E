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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

/**
 * An image cache that uses local SQLite databases
 */
public class DatabaseImageCache implements ImageCache {

    /**
     * The tag used for logging
     */
    private static final String TAG = DatabaseImageCache.class.getSimpleName();

    /**
     * The format used for compression
     */
    private static final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;

    /**
     * The compression quality level, 0-100 (ignored for PNG)
     */
    private static final int QUALITY = 100;


    /**
     * The helper used for database access
     */
    private final SQLiteOpenHelper mHelper;

    public DatabaseImageCache(Context context) {
        mHelper = new ImageOpenHelper(context);
    }

    @Override
    public long put(Bitmap image) throws CacheException {
        Objects.requireNonNull(image, "image must not be null");
        // Compress image into a byte array to calculate the CRC
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final CRCOutputStream crcStream = new CRCOutputStream(bytes);
        final boolean compressResult = image.compress(FORMAT, QUALITY, crcStream);
        if (!compressResult) {
            throw new CacheException("Failed to compress image");
        }

        final long crc = crcStream.getCrc();
        // Check for existing image
        try {
            final SQLiteDatabase db = mHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                // Select reference count
                final Cursor result = db.query(ImageOpenHelper.TABLE_NAME, array("refcount"),
                        "crc = ?", array(Long.toString(crc)), null, null, null);
                try {

                    if (result.getCount() > 0) {
                        // Increment reference count
                        if (!result.moveToFirst()) {
                            throw new CacheException("Failed to access first result");
                        }
                        final long refCount = result.getLong(result.getColumnIndexOrThrow(
                                "refcount"));

                        // Check reference count value
                        if (refCount == Long.MAX_VALUE) {
                            throw new CacheException("Image reference count is at maximum");
                        }

                        final ContentValues values = new ContentValues();
                        values.put("refcount", refCount + 1);
                        final long count = db.update(ImageOpenHelper.TABLE_NAME, values, "crc = ?",
                                array(Long.toString(crc)));
                        if (count != 1) {
                            throw new CacheException("Failed to update reference count");
                        }
                    } else {
                        // Insert image
                        final ContentValues values = new ContentValues();
                        values.put("crc", crc);
                        values.put("refcount", 1);
                        final byte[] imageBytes = bytes.toByteArray();
                        Log.v(TAG, "Image bytes: length = " + imageBytes.length);
                        values.put("data", imageBytes);
                        final long rowId = db.insert(ImageOpenHelper.TABLE_NAME, null, values);
                        if (rowId == -1) {
                            throw new CacheException("Failed to insert image");
                        }
                    }
                } finally {
                    result.close();
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (SQLiteException e) {
            throw new CacheException("Database error", e);
        }
        return crc;
    }

    @Override
    public Bitmap get(long id) throws CacheException {
        try {
            final SQLiteDatabase db = mHelper.getReadableDatabase();
            try {
                final Cursor result = db.query(ImageOpenHelper.TABLE_NAME, array("data"),
                        "crc = ?", array(Long.toString(id)), null, null, null);
                try {
                    Log.v(TAG, "Result count: " + result.getCount());
                    Log.v(TAG, "data index: " + result.getColumnIndexOrThrow("data"));
                    if (result.getCount() > 0) {
                        // Read and return image
                        Log.v(TAG, "Initial position: " + result.getPosition());
                        if (!result.moveToFirst()) {
                            throw new CacheException("Could not move to first result");
                        }
                        Log.v(TAG, "Updated position: " + result.getPosition());
                        Log.v(TAG, "Data column type: " + result.getType(result.getColumnIndexOrThrow("data")));
                        final byte[] imageBytes = result.getBlob(result.getColumnIndexOrThrow(
                                "data"));
                        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    } else {
                        // Not found
                        return null;
                    }
                } finally {
                    result.close();
                }
            } finally {
                db.close();
            }
        } catch (SQLiteException e) {
            throw new CacheException("Database error", e);
        }
    }

    @Override
    public Bitmap remove(long id) throws CacheException {
        try {
            final SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                // Search for an existing image
                final Cursor result = db.query(ImageOpenHelper.TABLE_NAME, array("refcount", "data"),
                        "crc = ?", array(Long.toString(id)), null, null, null);
                try {
                    if (result.getCount() > 0) {
                        // Read and return image
                        if (!result.moveToFirst()) {
                            throw new CacheException("Could not move to first result");
                        }
                        final byte[] imageBytes = result.getBlob(result.getColumnIndexOrThrow(
                                "data"));
                        final Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0,
                                imageBytes.length);
                        decrementReferenceCount(db, id);
                        db.setTransactionSuccessful();
                        return image;
                    } else {
                        // Not found
                        return null;
                    }
                } finally {
                    result.close();
                }
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (SQLiteException e) {
            throw new CacheException("Database error", e);
        }
    }

    /**
     * Decrements the reference count of the image with the specified CRC, and deletes the image
     * if the reference count reaches 0
     * @param db the database to access
     * @param crc the CRC of the image to manipulate
     */
    private void decrementReferenceCount(SQLiteDatabase db, long crc) throws CacheException {
        // Get current reference count
        final Cursor result = db.query(ImageOpenHelper.TABLE_NAME, array("refcount"), "crc = ?",
                array(Long.toString(crc)), null, null, null);
        try {
            if (result.getCount() > 0) {
                result.moveToFirst();
                // Get and update reference count
                long refCount = result.getLong(result.getColumnIndexOrThrow("refcount"));
                refCount -= 1;

                if (refCount == 0) {
                    // Delete image
                    if (db.delete(ImageOpenHelper.TABLE_NAME, "crc = ?", array(Long.toString(crc))) != 1) {
                        throw new CacheException("Failed to delete image with reference count 0");
                    }
                }
                else {
                    // Update reference count
                    final ContentValues values = new ContentValues();
                    values.put("refcount", refCount);
                    if (db.update(ImageOpenHelper.TABLE_NAME, values, "crc = ?", array(Long.toString(crc))) != 1) {
                        throw new CacheException("Failed to update reference count");
                    }
                }
            }
            else {
                throw new CacheException("No image found with requested ID");
            }
        }
        finally {
            result.close();
        }
    }

    @Override
    public void clear() throws CacheException {
        try {
            final SQLiteDatabase db = mHelper.getWritableDatabase();
            try {
                db.delete(ImageOpenHelper.TABLE_NAME, null, null);
            }
            finally {
                db.close();
            }
        }
        catch (SQLiteException e) {
            throw new CacheException("Database error", e);
        }
    }

    /**
     * Converts an argument list into an array
     * @param values the values to return
     * @param <T> the type of the values
     * @return an array containing the provided values
     */
    @SafeVarargs
    private static <T> T[] array(T... values) {
        return values;
    }


    /**
     * An OpenHelper used to access an image cache database
     */
    private static class ImageOpenHelper extends SQLiteOpenHelper {

        /**
         * The name of the database
         */
        private static final String DATABASE_NAME = "image_cache";
        /**
         * The name of the table
         */
        public static final String TABLE_NAME = "image_cache";
        /**
         * The column key for the image CRC32
         */
        public static final String COL_CRC = "crc";
        /**
         * The column key for the image reference count
         */
        public static final String COL_REFCOUNT = "refcount";
        /**
         * The column key for the image data
         */
        public static final String COL_DATA = "data";
        /**
         * The current database version
         */
        private static final int DATABASE_VERSION = 1;

        public ImageOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_CRC + " INTEGER PRIMARY KEY," +
                    " " + COL_REFCOUNT + " INTEGER NOT NULL DEFAULT 0," +
                    " data BLOB NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Implement later when new versions are introduced
        }
    }

    /**
     * An OutputStream that calculates the CRC32 checksum of the bytes written to it
     */
    private static class CRCOutputStream extends FilterOutputStream {

        /**
         * The CRC calculator
         */
        private CRC32 mCrc;

        /**
         * Creates a CRCOutputStream that wraps another stream
         *
         * @param inner the stream to wrap. Must not be null.
         */
        public CRCOutputStream(OutputStream inner) {
            super(inner);
            mCrc = new CRC32();
        }

        /**
         * Writes a byte to the underlying stream and updates the CRC32 checksum to include
         * the written byte.
         *
         * If the underlying stream throws an exception, the checksum is not updated.
         *
         * @param oneByte the byte to write
         * @throws IOException if an error occurred writing the byte
         */
        @Override
        public void write(int oneByte) throws IOException {
            super.write(oneByte);
            mCrc.update(oneByte);
        }

        /**
         * Returns the CRC32 checksum of the bytes written so far
         *
         * @return the checksum
         */
        public long getCrc() {
            return mCrc.getValue();
        }
    }
}
