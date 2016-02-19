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

import android.graphics.Bitmap;

/**
 * A cache that stores images
 *
 * Each image is associated with a 64-bit identifier that uniquely identifies it.
 *
 * Images stored in a cache are reference-counted. Each call to {@link #put(Bitmap)} increments
 * the reference count of that image, and each call to {@link #remove(long)} decrements it.
 * When an image's reference count reaches zero, the image is removed from the cache.
 */
public interface ImageCache {

    /**
     * An exception that indicates a caching failure
     */
    class CacheException extends Exception {
        public CacheException() {
        }

        public CacheException(String detailMessage) {
            super(detailMessage);
        }

        public CacheException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public CacheException(Throwable throwable) {
            super(throwable);
        }
    }

    /**
     * Inserts an image into the cache. After this method is called, the image can be retrieved
     * by calling {@link #get(long)} with the returned image identifier.
     *
     * If the same image is already in the cache, its reference count is incremented.
     *
     * @param image the image to insert
     * @return a value that uniquely identifies this image
     * @throws org.cook_e.data.ImageCache.CacheException if the image could not be stored
     */
    long put(Bitmap image) throws CacheException;

    /**
     * Locates and returns an image with a specified ID
     * @param id the ID of an image, as returned by {@link #put(Bitmap)}
     * @return the image, or null if no matching image could be found
     * @throws org.cook_e.data.ImageCache.CacheException if an error occurred
     */
    Bitmap get(long id) throws CacheException;

    /**
     * Retrieves an image with the specified ID and decrements its reference count.
     * @param id the ID of an image, as returned by {@link #put(Bitmap)}
     * @return the image from the cache, or null if no matching image could be found
     * @throws org.cook_e.data.ImageCache.CacheException if an error occurred
     */
    Bitmap remove(long id) throws CacheException;

    /**
     * Removes all images from this cache, regardless of their reference counts
     * @throws CacheException if an error occurred
     */
    void clear() throws CacheException;
}
