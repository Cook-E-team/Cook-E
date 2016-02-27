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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.cook_e.cook_e.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * An image cache that uses files
 */
public class FileImageCache implements ImageCache {

    /**
     * The tag used for logging
     */
    private static final String TAG = FileImageCache.class.getSimpleName();

    /**
     * The name of the directory in which images are stored
     */
    private static final String DIRECTORY_NAME = "image_cache";

    /**
     * The format used for compression
     */
    private static final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;

    /**
     * The compression quality level, 0-100 (ignored for PNG)
     */
    private static final int QUALITY = 100;

    /**
     * The instance
     */
    private static ImageCache instance;

    /**
     * The context used to access storage
     */
    private final Context mContext;

    /**
     * The directory in which files are stored
     */
    private final File mCacheDir;

    @Override
    public long put(Bitmap image) throws CacheException {
        File imageFile = null;
        try {
            // Compress image into a byte array to calculate the CRC
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final CrcOutputStream crc = new CrcOutputStream(bytes);
            final boolean result = image.compress(FORMAT, QUALITY, crc);
            if (!result) {
                throw new CacheException("Failed to compress image");
            }

            final long crcResult = crc.getCrc();
            // Write image
            imageFile = pathForCrc(crcResult);
            final FileOutputStream fileStream = new FileOutputStream(imageFile);
            fileStream.write(bytes.toByteArray());

            return crcResult;
        }
        catch (IOException e) {
            // Try to delete file, ignore errors
            //noinspection ResultOfMethodCallIgnored
            imageFile.delete();
            throw new CacheException("Failed to write image", e);
        }
    }

    @Override
    public Bitmap get(long id) throws CacheException {
        final File imageFile = pathForCrc(id);
        if (!imageFile.exists()) {
            return null;
        }
        return BitmapFactory.decodeFile(imageFile.getPath());
    }

    @Override
    public Bitmap remove(long id) throws CacheException {
        final File imageFile = pathForCrc(id);
        if (!imageFile.exists()) {
            return null;
        }
        final Bitmap loadedImage = BitmapFactory.decodeFile(imageFile.getPath());
        final boolean success = imageFile.delete();
        if (!success) {
            throw new CacheException("Failed to delete file " + imageFile.getPath());
        }
        return loadedImage;
    }

    @Override
    public void clear() throws CacheException {
        for (File file : mCacheDir.listFiles()) {
            final boolean success = file.delete();
            if (!success) {
                throw new CacheException("Failed to delete file " + file.getPath());
            }
        }
    }


    /**
     * Returns a File for an image with the specified CRC checksum
     * @param crc the checksum
     * @return a File
     */
    private File pathForCrc(long crc) {
        return new File(String.format(Locale.US, "%s%s%016x.png", mCacheDir.getPath(), File.separator, crc));
    }

    /**
     * Ensures that the storage directory exists, and returns a File that represents it
     * @return the storage directory as a File
     */
    private File getStorageDirectory() throws IOException {
        final File dir = new File(
                mContext.getCacheDir().getPath() + File.separator + DIRECTORY_NAME);
        // Ensure directory exists and is a directory
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                final boolean success = dir.delete();
                if (!success) {
                    throw new IOException("Could not delete non-directory file " + dir.getPath());
                }
            }
        }
        else {
            // Does not exist
            // Create as directory
            final boolean success = dir.mkdirs();
            if (!success) {
                throw new IOException("Could not create directory " + dir.getPath());
            }
        }
        return dir;
    }

    /**
     * Creates a new cache
     * @param context the context to use to access storage. Must not be null.
     */
    private FileImageCache(Context context) throws IOException {
        Objects.requireNonNull(context, "context must not be null");
        mContext = context;
        mCacheDir = getStorageDirectory();
    }

    /**
     * Returns the ImageCache used by the application
     * @return an ImageCache
     */
    public static ImageCache getInstance() {
        if (instance == null) {
            try {
                instance = new FileImageCache(App.getAppContext());
            } catch (IOException e) {
                // No ideal way to handle this
                Log.e(TAG, "Failed to set up image cache directory", e);
                return null;
            }
        }
        return instance;
    }
}
