package com.obviz.review.managers;

import java.io.File;
import java.net.URI;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by gaylor on 05-Aug-15.
 * Normal file implementation with a ReadWriteLock to manage the concurrency of the cache
 */
public class CacheFile extends File {

    private long expiration;

    public CacheFile(File dir, String name) {
        super(dir, name);

        expiration = 0;
    }

    public CacheFile(File dir, String name, long expiration) {
        this(dir, name);

        this.expiration = expiration;
    }

    public boolean isExpired() {
        return expiration > 0 && System.currentTimeMillis() > expiration;
    }
}
