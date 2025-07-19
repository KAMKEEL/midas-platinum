package havocx42;

/** Simple listener for reporting progress. */
public interface ProgressListener {
    /**
     * Called when a chunk has completed processing.
     *
     * @param completed number of chunks processed so far
     * @param total     total chunks to process
     */
    void update(int completed, int total);
}
