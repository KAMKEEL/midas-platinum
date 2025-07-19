package havocx42;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Utility for asynchronous conversion tasks. */
public final class AsyncUtil {
    /** Shared executor used for async tasks. */
    public static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private AsyncUtil() {
    }
}
