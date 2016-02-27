package org.cook_e.data;

import android.os.Handler;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wraps an {@link SQLAccessor} and provides asynchronous versions of its functionality
 *
 * Methods of this class must only be called from one thread (usually the main thread).
 * Result handlers will be invoked on that thread.
 */
public class AsyncAccessor {

    /**
     * Interface for objects that can be notified when a result or error is available
     * @param <T> the result type
     */
    public interface ResultHandler<T> {
        /**
         * Called when a result is available
         * @param result the result
         */
        void onResult(T result);

        /**
         * Called when the computation resulted in an exception
         * @param e the exception that was thrown
         */
        void onException(Exception e);
    }

    /**
     * The handler used to return results to the calling thread
     */
    private final Handler mHandler;

    /**
     * The underlying accessor
     */
    private final StorageAccessor mAccessor;

    /**
     * The executor service used to run tasks
     */
    private final ExecutorService mExecutorService;

    /**
     * Creates a new AsyncAccessor
     *
     * This constructor must be called from the thread that will call other methods and get results.
     *
     * No other code may access the accessor after this AsyncAccessor is created.
     *
     * @param accessor the accessor to wrap. The accessor class does not need to be thread-safe.
     */
    public AsyncAccessor(StorageAccessor accessor) {
        mAccessor = accessor;
        // Create a new Handler on the thread that called this method
        mHandler = new Handler();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Inserts a recipe into the database
     * @param recipe the recipe to insert
     * @param handler a handler to be notified if an error occurs (on success, the handler will be
     *                passed a null reference)
     */
    public void insertRecipe(Recipe recipe, final ResultHandler<?> handler) {
        final Recipe copy = new Recipe(recipe);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mAccessor) {
                        mAccessor.storeRecipe(copy);
                    }
                } catch (SQLException e) {
                    postException(handler, e);
                }
            }
        });
    }

    /**
     * Loads all available recipes from the database
     * @param handler a result handler
     */
    public void loadAllRecipes(final ResultHandler<? super List<Recipe>> handler) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Recipe> recipes;
                    synchronized (mAccessor) {
                        recipes = mAccessor.loadAllRecipes();
                    }
                    postResult(handler, recipes);
                } catch (SQLException e) {
                    postException(handler, e);
                }
            }
        });
    }

    public void updateRecipe(final Recipe r, final ResultHandler<?> handler) {
        final Recipe copy = new Recipe(r);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mAccessor) {
                        mAccessor.editRecipe(r);
                    }
                    postResult(handler, null);
                } catch (SQLException e) {
                    postException(handler, e);
                }
            }
        });
    }

    public void insertBunch(Bunch b, final ResultHandler<?> handler) {
        final Bunch copy = new Bunch(b.getTitle(), b.getRecipes());
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mAccessor) {
                        mAccessor.storeBunch(copy);
                    }
                    postResult(handler, null);
                } catch (SQLException e) {
                    postException(handler, e);
                }
            }
        });
    }

    public void loadAllBunches(final ResultHandler<List<Bunch>> handler) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Bunch> result;
                    synchronized (mAccessor) {
                        result = mAccessor.loadAllBunches();
                    }
                    postResult(handler, result);
                } catch (SQLException e) {
                    postException(handler, e);
                }
            }
        });
    }

    /**
     * Posts to {@link #mHandler} a Runnable that provides a result to a handler
     * @param handler the handler to provide a result to
     * @param result the result to provide
     * @param <T> the result type
     */
    private <T> void postResult(final ResultHandler<T> handler, final T result) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.onResult(result);
            }
        });
    }
    /**
     * Posts to {@link #mHandler} a Runnable that provides an exception to a handler
     * @param handler the handler to provide a result to
     * @param exception the exception to provide
     */
    private void postException(final ResultHandler<?> handler, final Exception exception) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.onException(exception);
            }
        });
    }
}
