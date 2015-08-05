package com.obviz.review;

import com.obviz.review.managers.DiskTimedCache;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Random;

public class DiskTimedCacheTest {

    private static final long CACHE_SIZE = 1024;
    private static final int EXPIRATION_TIME = 100000;

    private static final Random random = new Random();

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

        closeCache(cache);
    }

    @Test
    public void testOpenCloseCache() throws IOException {
        DiskTimedCache cache = initCache();

        for (int i = 0; i < 10; i++) {
            DiskTimedCache.Editor editor = cache.edit("string-"+i);
            ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
            stream.writeObject("String-" + i);

            editor.commit();
        }

        try {
            cache = DiskTimedCache.Factory.instance.open(new File(cache.getDirectory().getName()), 0, CACHE_SIZE);
        } catch (IOException e) {
            Assert.fail("Can't initialize the cache");
        }

        Assert.assertEquals("Stack cache size after reopened", 10, cache.getStack().size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("Stack value in good order", "string-" + (9 - i), cache.getStack().get(i).getName());
        }

        closeCache(cache);
    }

    @Test
    public void testEditKey() throws IOException {

        DiskTimedCache cache = initCache();

        DiskTimedCache.Editor editor = cache.edit("test-edit");
        ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
        stream.writeObject("Test string");

        editor.commit();

        File checkFile = new File(cache.getDirectory(), "test-edit");
        Assert.assertTrue("Cache file creation", checkFile.exists());

        closeCache(cache);
    }

    @Test
    public void testAccessKey() throws IOException, ClassNotFoundException {

        DiskTimedCache cache = initCache();

        for (int i = 0; i < 10; i++) {
            DiskTimedCache.Editor editor = cache.edit("string-"+i);
            ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
            stream.writeObject("String-" + i);

            editor.commit();
        }

        for (int i = 0; i < 10; i++) {
            DiskTimedCache.Accessor accessor = cache.get("string-"+i);
            ObjectInputStream stream = new ObjectInputStream(accessor.getStream());
            String object = (String) stream.readObject();

            Assert.assertTrue("Get value", object.equals("String-"+i));
            accessor.commit();
        }

        closeCache(cache);
    }

    @Test
    public void testStackManagement() throws IOException {
        DiskTimedCache cache = initCache();

        for (int i = 0; i < 10; i++) {
            DiskTimedCache.Editor editor = cache.edit("string-"+i);
            ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
            stream.writeObject("String-" + i);

            editor.commit();
        }

        Assert.assertEquals("Stack size", 10, cache.getStack().size());

        for (int i = 0; i < 10; i++) {

            Assert.assertEquals("Stack value", cache.getStack().get(i).getName(), "string-" + (9-i));
        }

        DiskTimedCache.Accessor accessor = cache.get("string-5");
        accessor.commit();

        Assert.assertEquals("Stack get value", cache.getStack().get(0).getName(), "string-5");

        closeCache(cache);
    }

    @Test
    public void testCacheSize() throws IOException {
        DiskTimedCache cache = initCache();

        for (int i = 0; i < 100; i++) {
            DiskTimedCache.Editor editor = cache.edit("string-"+i);
            ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
            stream.writeObject("String-" + i);

            editor.commit();
        }

        Assert.assertTrue("Cache size test", directorySize(cache) < CACHE_SIZE);

        closeCache(cache);
    }

    @Test
    public void testExpirationTime() throws IOException, InterruptedException {
        DiskTimedCache cache = initCache();

        DiskTimedCache.Editor editor = cache.edit("test-expiration", 1000);
        ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
        stream.writeObject("Test");

        editor.commit();

        Thread.sleep(2000);

        DiskTimedCache.Accessor accessor = cache.get("test-expiration");
        Assert.assertNull("Expired object test", accessor);

        editor = cache.edit("test-expiration", EXPIRATION_TIME);
        stream = new ObjectOutputStream(editor.getStream());
        stream.writeObject("Test2");

        editor.commit();

        accessor = cache.get("test-expiration");
        Assert.assertNotNull("Non expired object test", accessor);
        accessor.commit();

        closeCache(cache);
    }

    @Test
    public void testMultipleAllocation() throws IOException {
        DiskTimedCache cache = initCache();

        DiskTimedCache.Editor editorTest = cache.edit("test-allocations");
        ObjectOutputStream s = new ObjectOutputStream(editorTest.getStream());
        s.writeObject("test");
        editorTest.commit();

        for (int i = 0; i < 100; i++) {
            DiskTimedCache.Editor editor = cache.edit("string-"+i);
            ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
            stream.writeObject("String-"+i);

            editor.commit();

            DiskTimedCache.Accessor accessor = cache.get("test-allocations");

            accessor.commit();
        }

        DiskTimedCache.Accessor accessor = cache.get("test-allocations");
        Assert.assertNotNull(accessor);
        accessor.commit();

        closeCache(cache);
    }

    @Test
    public void testMultipleAllocationStates() throws IOException {
        DiskTimedCache cache = initCache();

        for (int i = 0; i < 100; i++) {
            DiskTimedCache.Editor editor = cache.edit("string-"+i);
            ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
            stream.writeObject("String-"+i);

            editor.commit();
        }

        // -1 because of journal file
        Assert.assertEquals("Stack size and files' number", cache.getStack().size(), cache.getDirectory().list().length - 1);

        closeCache(cache);
    }

    @Test
    public void testGeneralUsage() throws IOException {
        DiskTimedCache cache = initCache();

        for (int i = 0; i < 10; i++) {
            DiskTimedCache.Editor editor = cache.edit("usage-"+i, 10000);
            ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
            stream.writeObject("String-"+i);
            editor.commit();
        }

        DiskTimedCache.Editor editor = cache.edit("usage-5", 10000);
        ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
        stream.writeObject("String-5");
        editor.commit();

        DiskTimedCache.Accessor accessor = cache.get("usage-2");
        Assert.assertNotNull(accessor);
        accessor.commit();

        Assert.assertEquals("Value in stack", "usage-2", cache.getStack().get(0).getName());
        Assert.assertEquals("Value in stack", "usage-5", cache.getStack().get(1).getName());

        closeCache(cache);
    }

    @Test
    public void testEmptyCache() {
        DiskTimedCache cache = DiskTimedCache.Factory.instance.empty();

        for (int i = 0; i < 10; i++) {

            DiskTimedCache.Editor editor = cache.edit("usage-"+i);
            Assert.assertNull("Editor must be null", editor);
        }

        for (int i = 0; i < 10; i++) {

            DiskTimedCache.Accessor accessor = cache.get("usage-"+i);
            Assert.assertNull("Editor must be null", accessor);
        }
    }

    private long directorySize(DiskTimedCache cache) {

        long size = 0;
        for (String filename : cache.getDirectory().list()) {
            if (!filename.equals("journal")) {
                size += new File(cache.getDirectory(), filename).length();
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

    private DiskTimedCache initCache() {
        File file = new File(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
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

    private void closeCache(DiskTimedCache cache) {
        cache.close();
        Assert.assertTrue(deleteDirectory(cache.getDirectory()));
    }
}