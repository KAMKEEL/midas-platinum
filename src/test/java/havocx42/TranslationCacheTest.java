package havocx42;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TranslationCacheTest {
    @Test
    public void cachesResults() {
        HashMap<BlockUID, BlockUID> map = new HashMap<BlockUID, BlockUID>();
        final BlockUID key = new BlockUID(1, 0);
        BlockUID val = new BlockUID(2, 0);
        map.put(key, val);
        TranslationCache cache = new TranslationCache(map);
        BlockUID r1 = cache.get(key);
        assertEquals(val, r1);
        map.put(key, new BlockUID(3, 0));
        BlockUID r2 = cache.get(key);
        assertSame(r1, r2);
    }

    @Test
    public void threadSafeAccess() throws Exception {
        HashMap<BlockUID, BlockUID> map = new HashMap<BlockUID, BlockUID>();
        final BlockUID key = new BlockUID(1, 0);
        BlockUID val = new BlockUID(2, 0);
        map.put(key, val);
        final TranslationCache cache = new TranslationCache(map);
        ExecutorService ex = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            ex.submit(new Runnable() {
                public void run() {
                    cache.get(key);
                }
            });
        }
        ex.shutdown();
        assertTrue(ex.awaitTermination(5, TimeUnit.SECONDS));
    }
}
