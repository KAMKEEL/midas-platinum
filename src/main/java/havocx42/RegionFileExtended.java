package havocx42;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import havocx42.AsyncUtil;
import java.util.logging.Level;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.Tag;

import pfaeff.IDChanger;

public class RegionFileExtended extends region.RegionFile {

    public RegionFileExtended(File path) {
        super(path);
    }

    public int countChunks() {
        int count = 0;
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                if (hasChunk(x, z)) {
                    count++;
                }
            }
        }
        return count;
    }

    public void convert(final RegionFileExtended output,
            final HashMap<BlockUID, BlockUID> translations, final ArrayList<ConverterPlugin> regionPlugins,
            final java.util.concurrent.atomic.AtomicInteger completedChunks, final int totalChunks,
            final ProgressListener listener) throws IOException {

        // Progress

        // System.out.println("Processing file " + rf.getFile());
        ArrayList<Point> chunks = new ArrayList<Point>();

        // Get available chunks
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                if (hasChunk(x, z)) {
                    chunks.add(new Point(x, z));
                }
            }
        }

        // LOG how many chunks are in this region
        IDChanger.logger.log(Level.INFO, "Chunks: " + chunks.size());

        int count = 0;
        for (Point p : chunks) {
            try (DataInputStream input = getChunkDataInputStream(p.x, p.y)) {
                CompoundTag root = NbtIo.read(input);
                for (ConverterPlugin plugin : regionPlugins) {
                    plugin.convert(root, translations);
                }

                try (DataOutputStream out = output.getChunkDataOutputStream(p.x, p.y)) {
                    NbtIo.write(root, out);
                }
            }

            int done = completedChunks.incrementAndGet();
            if (listener != null) {
                listener.update(done, totalChunks);
            }
            count++;
            IDChanger.logger.log(Level.INFO,
                    "Chunk " + count + "/" + chunks.size() + " complete in " + fileName.getName());
        }
    }
}
