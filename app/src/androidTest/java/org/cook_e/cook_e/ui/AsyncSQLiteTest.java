package org.cook_e.cook_e.ui;

import android.support.test.InstrumentationRegistry;

import org.cook_e.data.AsyncAccessor;
import org.cook_e.data.Recipe;
import org.cook_e.data.SQLAccessor;
import org.cook_e.data.SQLiteAccessor;
import org.cook_e.data.StorageParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests a {@link org.cook_e.data.AsyncAccessor} wrapping an {@link org.cook_e.data.SQLiteAccessor}
 */
public class AsyncSQLiteTest {

    /**
     * The accessor
     */
    private AsyncAccessor mAccessor;

    @Before
    public void setUp() throws InterruptedException {
        final SQLAccessor accessor = new SQLiteAccessor(InstrumentationRegistry.getTargetContext(),
                new StorageParser());
        mAccessor = new AsyncAccessor(accessor);
        final CountDownLatch latch = new CountDownLatch(1);
        mAccessor.clearAllTables(new AsyncAccessor.ResultHandler<Object>() {
            @Override
            public void onResult(Object result) {
                latch.countDown();
            }

            @Override
            public void onException(Exception e) {
                failWithException(e);
            }
        });
        latch.await();
    }

    @After
    public void tearDown() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mAccessor.clearAllTables(new AsyncAccessor.ResultHandler<Object>() {
            @Override
            public void onResult(Object result) {
                latch.countDown();
            }

            @Override
            public void onException(Exception e) {
                failWithException(e);
            }
        });
        latch.await();
    }

    @Test
    public void testLoadAllNoRecipes() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Recipe> expected = Collections.emptyList();
        mAccessor.loadAllRecipes(new AsyncAccessor.ResultHandler<List<Recipe>>() {
            @Override
            public void onResult(List<Recipe> result) {
                assertEquals(expected, result);
                latch.countDown();
            }

            @Override
            public void onException(Exception e) {
                failWithException(e);
            }
        });
        latch.await();
    }

    private void failWithException(Exception e) {
        fail("Unexpected exception: " + e.getClass().getName() + ":" + e.getLocalizedMessage());
    }
}
