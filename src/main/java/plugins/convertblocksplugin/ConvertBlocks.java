package plugins.convertblocksplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.Tag;

import havocx42.BlockUID;
import havocx42.ConverterPlugin;
import havocx42.PluginType;
import havocx42.Section;
import havocx42.Status;
import pfaeff.IDChanger;

import havocx42.TranslationCache;

public class ConvertBlocks implements ConverterPlugin {

	private int warnUnconvertedAfter;
	private boolean countBlockStats;
	public static final int BLOCKS_PER_EBS = 4096;

	public ConvertBlocks(Integer warnUnconvertedAfter, boolean countBlockStats) {
		if (warnUnconvertedAfter == null) {
			this.warnUnconvertedAfter = -1;
		} else {
			this.warnUnconvertedAfter = warnUnconvertedAfter; 	
		}
		this.countBlockStats = countBlockStats;
	}

	@Override
	public String getPluginName() {
		return "Convert Blocks";
	}

	@Override

       public void convert(Tag root, final HashMap<BlockUID, BlockUID> translations) {
               ArrayList<Tag> result = new ArrayList<Tag>();
               root.findAllChildrenByName(result, "Sections", true);
               final TranslationCache cache = new TranslationCache(translations);
               CompoundTag sectionTag;
               for (Tag list : result) {
                       if (list instanceof ListTag) {
                               ListTag<CompoundTag> sections = (ListTag<CompoundTag>) list;
                               for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
                                       sectionTag = sections.get(sectionIndex);
                                       final Section section = new Section(sectionTag);
                                       convertSection(section, cache);
                               }
                       }
               }
        }

       private void convertSection(Section section, TranslationCache cache) {
               // Cache conversions per unique block to minimise lookups
               Map<BlockUID, BlockUID> palette = new HashMap<BlockUID, BlockUID>();

               // Arrays used for NEID block conversion
               byte[] newBlocks = new byte[section.length()];
               byte[] newData = new byte[section.dataTag.data.length];
               System.arraycopy(section.dataTag.data, 0, newData, 0, newData.length);
               byte[] newAdd = null;
               short[] newBlocks16 = new short[section.length()];

               boolean converted = false;

               for (int i = 0; i < section.length(); i++) {
                       BlockUID src = section.getBlockUID(i);
                       BlockUID translated = palette.get(src);
                       if (translated == null) {
                               translated = cache.get(src);
                               palette.put(src, translated);
                       }

                       BlockUID out = translated != null ? translated : src;
                       if (translated != null) {
                               converted = true;
                               IDChanger.changedPlaced.incrementAndGet();
                               if (countBlockStats) {
                                       Integer count = IDChanger.convertedBlockCount.get(src);
                                       if (count == null) {
                                               IDChanger.convertedBlockCount.put(src, 1);
                                       } else {
                                               IDChanger.convertedBlockCount.put(src, count + 1);
                                       }
                               }
                       } else if (warnUnconvertedAfter != -1 && src.blockID > warnUnconvertedAfter) {
                               System.out.println("untranslated block:" + src);
                       }

                       // write block id arrays
                       newBlocks[i] = (byte) (out.blockID & 0xFF);
                       newBlocks16[i] = (short) out.blockID.intValue();
                       if (out.blockID > 255) {
                               if (newAdd == null) newAdd = new byte[2048];
                               int nibbleIndex = i >> 1;
                               boolean low = (i & 1) == 0;
                               if (low) {
                                       newAdd[nibbleIndex] = (byte) ((newAdd[nibbleIndex] & 0xF0) | (out.blockID >> 8 & 0xF));
                               } else {
                                       newAdd[nibbleIndex] = (byte) ((newAdd[nibbleIndex] & 0x0F) | ((out.blockID >> 4) & 0xF0));
                               }
                       }

                       Integer dataVal = out.dataValue;
                       if (dataVal != null && dataVal < 16) {
                               int nibbleIndex = i >> 1;
                               boolean low = (i & 1) == 0;
                               if (low) {
                                       newData[nibbleIndex] = (byte) ((newData[nibbleIndex] & 0xF0) | (dataVal & 0xF));
                               } else {
                                       newData[nibbleIndex] = (byte) ((newData[nibbleIndex] & 0x0F) | ((dataVal & 0xF) << 4));
                               }
                       }
               }

               if (converted) {
                       section.blocksTag.data = newBlocks;
                       section.dataTag.data = newData;
                       if (newAdd != null) {
                               if (section.addTag == null) {
                                       section.addTag = new ByteArrayTag("Add", newAdd);
                                       section.sectionTag.put("Add", section.addTag);
                               } else {
                                       section.addTag.data = newAdd;
                               }
                       }
                       section.block16BArray = newBlocks16;
                       section.blocks16 = new ByteArrayTag("Blocks16", section.getBlockData16());
                       section.sectionTag.put("Blocks16", section.blocks16);
               }
       }
	@Override
	public PluginType getPluginType() {
		return PluginType.REGION;
	}
}
