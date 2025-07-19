package havocx42;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Utility for asynchronous conversion tasks. */
public final class AsyncUtil {
    /** Executor for region-level tasks. */
    public static final ExecutorService REGION_EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /** Executor for chunk/section level tasks to avoid deadlocks. */
    public static final ExecutorService SECTION_EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private AsyncUtil() {
    }
}
