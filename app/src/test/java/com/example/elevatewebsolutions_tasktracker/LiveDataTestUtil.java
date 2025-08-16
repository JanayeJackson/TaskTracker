package com.example.elevatewebsolutions_tasktracker;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * utility class for testing livedata synchronously
 * allows tests to wait for livedata values without using async observers
 */
public class LiveDataTestUtil {

    /**
     * gets the value from livedata or waits until it has one
     * with a timeout to avoid hanging tests
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);

        try {
            // wait for livedata to emit a value
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw new RuntimeException("livedata value was never set");
            }
        } finally {
            liveData.removeObserver(observer);
        }

        //noinspection unchecked
        return (T) data[0];
    }

    /**
     * gets livedata value with custom timeout
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData, long timeout, TimeUnit timeUnit)
            throws InterruptedException, TimeoutException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);

        try {
            if (!latch.await(timeout, timeUnit)) {
                throw new TimeoutException("livedata value was never set within timeout");
            }
        } finally {
            liveData.removeObserver(observer);
        }

        //noinspection unchecked
        return (T) data[0];
    }
}
