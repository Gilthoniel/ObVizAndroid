package com.obviz.review;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.obviz.review.managers.DiskTimedCache;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.Random;

public class DiskTimedCacheTest {

    private static final long CACHE_SIZE = 1024;

    @Test
    public void testInitCache() {

        File file = new File("cache");
        file.mkdirs();

        DiskTimedCache cache = null;
        try {
            cache = DiskTimedCache.Factory.instance.open(file, 0, CACHE_SIZE);
        } catch (IOException e) {
            Assert.fail("Can't initialize the cache");
        }
    }

    @Test
    public void testEditKey() throws IOException {

        File dir = new File("__edit");
        DiskTimedCache cache = initCache(dir);

        cache.add("test-edit", "Test string");

        File checkFile = new File("__edit", "test-edit");
        Assert.assertTrue("Cache file creation", checkFile.exists());

        deleteDirectory(dir);
    }

    @Test
    public void testAccessKey() throws IOException, ClassNotFoundException {

        File dir = new File("__access");
        DiskTimedCache cache = initCache(dir);

        for (int i = 0; i < 10; i++) {
            cache.add("string-"+i, "string-"+i);
        }

        for (int i = 0; i < 10; i++) {

            Assert.assertTrue("Get value", cache.get("string-"+i).equals("string-"+i));
        }

        deleteDirectory(dir);
    }

    @Test
    public void testStackManagement() throws IOException {

        File dir = new File("__stack");
        DiskTimedCache cache = initCache(dir);

        LinkedList<String> stack = new LinkedList<>();
        cache.setStack(stack);

        for (int i = 0; i < 10; i++) {
            cache.add("string"+i, "string"+i);
        }

        Assert.assertTrue("Must be in cache", cache.contains("string9"));
        cache.get("string9");
        Assert.assertTrue("Must be in cache", cache.contains("string7"));
        cache.get("string7");

        Assert.assertEquals("Invalid stack value", stack.get(0), "string7");
        Assert.assertEquals("Invalid stack value", stack.get(1), "string9");
        Assert.assertEquals("Invalid stack value", stack.get(9), "string0");
    }

    @Test
    public void testCacheSize() throws IOException {

        File dir = new File("__size");
        DiskTimedCache cache = initCache(dir);

        for (int i = 0; i < 50; i++) {
            cache.add("string"+i, "striiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiing");
        }

        Assert.assertTrue("Cache size test", directorySize(dir) < CACHE_SIZE);

        deleteDirectory(dir);
    }

    @Test
    public void testExpirationTime() throws IOException, InterruptedException {

        File dir = new File("__expiration");
        DiskTimedCache cache = initCache(dir);

        cache.add("expiration", "expiration", System.currentTimeMillis() + 500);
        Assert.assertTrue(cache.contains("expiration"));

        Thread.sleep(600);

        Assert.assertFalse(cache.contains("expiration"));

        cache.add("immortal", "immortal");

        Assert.assertTrue(cache.contains("immortal"));

        deleteDirectory(dir);
    }

    @Test
    public void testEmptyCache() {
        DiskTimedCache cache = DiskTimedCache.Factory.instance.empty();

        for (int i = 0; i < 10; i++) {

            cache.add("string"+i, "string"+i);
        }

        for (int i = 0; i < 10; i++) {

            Assert.assertFalse("Editor must be null", cache.contains("string"+i));
        }
    }

    private long directorySize(File dir) {

        long size = 0;
        for (String filename : dir.list()) {
            if (!filename.equals("journal")) {
                size += new File(dir, filename).length();
            }
        }

        return size;
    }

    private boolean deleteDirectory(File dir) {

        String[] entries = dir.list();
        for (String file: entries) {
            File currentFile = new File(dir.getPath(), file);
            currentFile.delete();
        }

        return dir.delete();
    }

    private DiskTimedCache initCache(File file) {
        file.mkdirs();
        DiskTimedCache cache;
        try {
            cache = DiskTimedCache.Factory.instance.open(file, 0, CACHE_SIZE);
        } catch (IOException e) {
            cache = null;
            Assert.fail("Can't initialize the cache");
        }

        return cache;
    }
}