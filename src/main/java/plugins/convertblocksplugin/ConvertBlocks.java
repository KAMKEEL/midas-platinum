package plugins.convertblocksplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.Tag;

import havocx42.BlockUID;
import havocx42.ConverterPlugin;
import havocx42.PluginType;
import havocx42.Section;
import havocx42.Status;
import pfaeff.IDChanger;

import havocx42.AsyncUtil;
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
               List<Future<?>> futures = new ArrayList<Future<?>>();
               CompoundTag sectionTag;
               for (Tag list : result) {
                       if (list instanceof ListTag) {
                               ListTag<CompoundTag> sections = (ListTag<CompoundTag>) list;
                               for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
                                       sectionTag = sections.get(sectionIndex);
                                       final Section section = new Section(sectionTag);
                                       futures.add(AsyncUtil.SECTION_EXECUTOR.submit(new Runnable() {
                                               @Override
                                               public void run() {
                                                       convertSection(section, cache);
                                               }
                                       }));
                               }
                       }
               }

                for (Future<?> f : futures) {
                        try {
                                f.get();
                        } catch (Exception e) {
                                throw new RuntimeException(e);
                        }
                }
        }

       private void convertSection(Section section, TranslationCache cache) {
               HashMap<Integer, BlockUID> indexToBlockIDs = new HashMap<Integer, BlockUID>();
               boolean found = false;

               for (int i = 0; i < section.length(); i++) {
                       BlockUID blockUID = section.getBlockUID(i);
                       BlockUID target = cache.get(blockUID);
                       if (target != null) {
                               IDChanger.changedPlaced.incrementAndGet();
                               found = true;
                               if (target.dataValue == null || target.dataValue < 16) {
                                       indexToBlockIDs.put(Integer.valueOf(i), target);
                               }
                               if (countBlockStats) {
                                       Integer count = IDChanger.convertedBlockCount.get(blockUID);
                                       if (count == null) {
                                               IDChanger.convertedBlockCount.put(blockUID, 1);
                                       } else {
                                               IDChanger.convertedBlockCount.put(blockUID, count + 1);
                                       }
                               }
                       } else {
                               if (warnUnconvertedAfter != -1 && blockUID.blockID > warnUnconvertedAfter) {
                                       System.out.println("untranslated block:" + blockUID);
                               }
                       }
               }

                if (found) {
                        for (int i = 0; i < section.length(); i++) {
                                BlockUID blockUID = section.getBlockUID(i);
                                section.setBlockID(i, blockUID.blockID);
                        }
                }

                Set<Map.Entry<Integer, BlockUID>> set = indexToBlockIDs.entrySet();
                for (Entry<Integer, BlockUID> entry : set) {
                        section.setBlockUID(entry.getKey(), entry.getValue());
                }
        }
	@Override
	public PluginType getPluginType() {
		return PluginType.REGION;
	}
}
