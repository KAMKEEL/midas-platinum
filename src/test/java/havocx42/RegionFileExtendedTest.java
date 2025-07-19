package havocx42;

import static org.junit.Assert.*;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;

import havocx42.ProgressListener;
import havocx42.RegionFileExtended;
import havocx42.BlockUID;
import havocx42.ConverterPlugin;

public class RegionFileExtendedTest {
    @Test
    public void reportsProgressForChunks() throws Exception {
        File input = Files.createTempFile("region", ".mca").toFile();
        File output = Files.createTempFile("region_out", ".mca").toFile();

        RegionFileExtended in = new RegionFileExtended(input);
        CompoundTag root = new CompoundTag("");
        root.put("Sections", new ListTag<CompoundTag>("Sections"));
        try (DataOutputStream out = in.getChunkDataOutputStream(0, 0)) {
            NbtIo.write(root, out);
        }
        in.close();

        in = new RegionFileExtended(input);
        RegionFileExtended outRf = new RegionFileExtended(output);

        final AtomicInteger completed = new AtomicInteger();
        int total = in.countChunks();

        in.convert(outRf, new HashMap<BlockUID, BlockUID>(), new ArrayList<ConverterPlugin>(), completed, total,
                new ProgressListener() {
                    @Override
                    public void update(int done, int tot) {
                        completed.set(done);
                    }
                });

        assertEquals(total, completed.get());

        in.close();
        outRf.close();
        input.delete();
        output.delete();
    }
}
