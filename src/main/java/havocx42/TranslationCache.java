package havocx42;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Simple cache for block translations to avoid repeated map lookups. */
public final class TranslationCache {
    private final Map<BlockUID, BlockUID> translations;
    private final ConcurrentHashMap<BlockUID, BlockUID> cache = new ConcurrentHashMap<BlockUID, BlockUID>();

    public TranslationCache(Map<BlockUID, BlockUID> translations) {
        this.translations = translations;
    }

    /**
     * Returns the translated block or {@code null} if none exists.
     */
    public BlockUID get(BlockUID key) {
        BlockUID result = cache.get(key);
        if (result != null) {
            return result;
        }
        result = translations.get(key);
        if (result != null) {
            cache.put(key, result);
        }
        return result;
    }
}
