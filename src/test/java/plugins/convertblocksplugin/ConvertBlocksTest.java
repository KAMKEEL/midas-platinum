package plugins.convertblocksplugin;
import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.mojang.nbt.ByteArrayTag;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.Tag;

import havocx42.BlockUID;
import havocx42.Section;
import pfaeff.IDChanger;

public class ConvertBlocksTest {
    @Test
    public void convertsSectionAsync() {
        HashMap<BlockUID, BlockUID> map = new HashMap<BlockUID, BlockUID>();
        map.put(new BlockUID(1, 0), new BlockUID(2, 0));
        ByteArrayTag blocks = new ByteArrayTag("Blocks", new byte[] {1});
        ByteArrayTag data = new ByteArrayTag("Data", new byte[] {0});
        CompoundTag section = new CompoundTag("");
        section.put("Blocks", blocks);
        section.put("Data", data);
        ListTag<CompoundTag> sections = new ListTag<CompoundTag>("Sections");
        sections.add(section);
        CompoundTag root = new CompoundTag("Level");
        root.put("Sections", sections);

        ConvertBlocks conv = new ConvertBlocks(null, false);
        IDChanger.changedPlaced.set(0);
        conv.convert(root, map);

        Section sec = new Section(section);
        assertEquals(2, sec.getBlockID(0).intValue());
        assertEquals(1, IDChanger.changedPlaced.get());
    }
}
