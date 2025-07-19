package havocx42;

import com.mojang.nbt.ByteArrayTag;
import com.mojang.nbt.CompoundTag;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class Section {
	public CompoundTag sectionTag;
	public ByteArrayTag addTag;
	public ByteArrayTag blocksTag;
	public ByteArrayTag dataTag;
	public ByteArrayTag blocks16;

        public static final int BLOCKS_PER_EBS = 4096;
	public short[] block16BArray;

	public Section(CompoundTag sectionTag) {
		super();
		this.sectionTag = sectionTag;
		blocksTag = (ByteArrayTag) sectionTag.findChildByName("Blocks", false);
		addTag = (ByteArrayTag) sectionTag.findChildByName("Add", false);
		blocks16 = (ByteArrayTag) sectionTag.findChildByName("Blocks16", false);
		dataTag = (ByteArrayTag) sectionTag.findChildByName("Data", false);

		block16BArray = new short[BLOCKS_PER_EBS];
	}

	public int length() {
		return blocksTag.data.length;
	}

	public BlockUID getBlockUID(int i) {
		return new BlockUID(this.getBlockID(i), this.getDataValue(i));
	}

	public void setBlockUID(int i, BlockUID value) {
		this.setBlockID(i, value.blockID);
		if (value.dataValue != null) {
			this.setDataValue(i, value.dataValue);
		}
	}

	public Integer getBlockID(int i) {
		Integer bID = Integer.valueOf(0x000000FF & (int) (blocksTag.data[i]));
		if (addTag != null) {
			int j = (i % 2 == 0) ? i / 2 : (i - 1) / 2;
			Integer aID = (i % 2 == 0) ? 0x0000000F & (int) addTag.data[j]
					: ((0x000000F0 & (int) addTag.data[j]) >> 4);
			bID += (aID << 8);
		}
		return bID;
	}

	public Integer getDataValue(int i) {
		int j = (i % 2 == 0) ? i / 2 : (i - 1) / 2;
		Integer dataValue = (i % 2 == 0) ? 0x0000000F & (int) dataTag.data[j]
				: ((0x000000F0 & (int) dataTag.data[j]) >> 4);
		return dataValue;
	}

	public void placeBlock(int i, int value) {
		final int bID = value & 0xFFFF;
		if (bID <= 255) {
			blocksTag.data[i] = (byte) bID;
		} else {
			if (addTag == null) {
				byte[] bytes = new byte[2048];
				addTag = new ByteArrayTag("Add", bytes);
				sectionTag.put("Add", addTag);
			}
			blocksTag.data[i] = (byte) bID;
			if (i % 2 == 0) {
				final int n = i / 2;
				addTag.data[n] |= (byte) (bID >>> 8 & 0xF);
			} else {
				int n2 = i / 2;
				addTag.data[n2] |= (byte) (bID >>> 4 & 0xF0);
			}
		}

		block16BArray[i] = (short) value;
		blocks16 = new ByteArrayTag("Blocks16", getBlockData16());
		sectionTag.put("Blocks16", blocks16);
	}

	public void setBlockID(int i, int value) {
		final int bID = value & 0xFFFF;
		if (bID <= 255) {
			blocksTag.data[i] = (byte) bID;
		} else {
			if (addTag == null) {
				byte[] bytes = new byte[2048];
				addTag = new ByteArrayTag("Add", bytes);
				sectionTag.put("Add", addTag);
			}
			blocksTag.data[i] = (byte) bID;
			if (i % 2 == 0) {
				final int n = i / 2;
				addTag.data[n] |= (byte) (bID >>> 8 & 0xF);
			} else {
				int n2 = i / 2;
				addTag.data[n2] |= (byte) (bID >>> 4 & 0xF0);
			}
		}

		block16BArray[i] = (short) value;
		blocks16 = new ByteArrayTag("Blocks16", getBlockData16());
		sectionTag.put("Blocks16", blocks16);
	}

	public void setDataValue(int i, int value) {
		int j = (i % 2 == 0) ? i / 2 : (i - 1) / 2;
		int dataByte = (int) dataTag.data[j];

		int newDataByte = (i % 2 == 0) ? ((0x0000000F & value)) | (dataByte & 0x000000F0)
				: ((0x0000000F & value) << 4) | (dataByte & 0x0000000F);
		dataTag.data[j] = (byte) newDataByte;
	}

	public byte[] getBlockData16() {
		final byte[] ret = new byte[this.block16BArray.length * 2];
		ByteBuffer.wrap(ret).asShortBuffer().put(this.block16BArray);
		return ret;
	}
}