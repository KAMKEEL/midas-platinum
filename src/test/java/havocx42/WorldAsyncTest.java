package havocx42;

import static org.junit.Assert.*;

import java.io.File;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.util.HashMap;

import org.junit.Test;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;

import joptsimple.OptionParser;
import joptsimple.OptionSet;


public class WorldAsyncTest {
    @Test
    public void convertsWorldAsync() throws Exception {
        File inputDir = Files.createTempDirectory("world_in").toFile();
        File regionDir = new File(inputDir, "region");
        regionDir.mkdirs();
        File regionFile = new File(regionDir, "r.0.0.mca");
        RegionFileExtended in = new RegionFileExtended(regionFile);
        CompoundTag root = new CompoundTag("");
        root.put("Sections", new ListTag<CompoundTag>("Sections"));
        try (DataOutputStream out = in.getChunkDataOutputStream(0, 0)) {
            NbtIo.write(root, out);
        }
        in.close();

        File outputDir = Files.createTempDirectory("world_out").toFile();

        World world = new World(inputDir, outputDir);
        OptionParser parser = new OptionParser();
        OptionSet opts = parser.parse();
        world.convert(new HashMap<BlockUID, BlockUID>(), opts);

        File outRegion = new File(outputDir, "region/r.0.0.mca");
        assertTrue(outRegion.exists());

        // cleanup
        for (File f : outputDir.listFiles()) {
            f.delete();
        }
        outputDir.delete();
        for (File f : regionDir.listFiles()) {
            f.delete();
        }
        regionDir.delete();
        inputDir.delete();
    }
}
