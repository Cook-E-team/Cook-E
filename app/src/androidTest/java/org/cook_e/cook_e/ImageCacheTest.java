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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;

import org.cook_e.data.ImageCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Tests for all implementations of {@link org.cook_e.data.ImageCache}
 */
public abstract class ImageCacheTest {

    /**
     * The cache under test
     */
    private ImageCache mCache;

    /**
     * The context used to access resources
     */
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        mCache = getImageCache(mContext);
    }

    @After
    public void tearDown() throws ImageCache.CacheException {
        mCache.clear();
        mCache = null;
    }

    @Test
    public void testEmpty() throws ImageCache.CacheException {
        for (long i = -342; i < 902 ; i++) {
            assertNull(mCache.get(i));
        }
    }

    @Test
    public void testInsertOne() throws ImageCache.CacheException {
        final Bitmap image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.small_image);

        final long id = mCache.put(image);

        final Bitmap retrieved = mCache.get(id);

        assertNotNull(retrieved);
        assertTrue(image.sameAs(retrieved));

        final Bitmap removed = mCache.remove(id);
        assertNotNull(removed);
        assertTrue(image.sameAs(removed));

        // Remove again
        final Bitmap notFound = mCache.remove(id);
        assertNull(notFound);
    }

    /**
     * Returns the image cache that should be used for testing
     * @param context the context to create a cache in
     * @return the image cache
     */
    protected abstract ImageCache getImageCache(Context context);
}
