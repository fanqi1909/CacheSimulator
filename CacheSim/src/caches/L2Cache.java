package caches;

import static conf.Constants.*;

import java.util.Random;

/**
 * L2 cache is 256Kb, block size 64 byte, 8-way set associative L2 is
 * write-through, write allocate
 * 
 * in L2 cache, each memory address is of the following form:
 * |--2-bit--op|-----47
 * -bit----tag|------9-bit--set-index---|----6-bit-offset---| 63 62 61 15 14 6 5
 * 0
 * 
 * 
 */

public class L2Cache {
	private long[][] cache;
	public L2Cache() {
		cache = new long[L2SETS][8];
	}
	/**
	 * read data in cache
	 * 
	 * @param addr
	 */
	public void read(long addr) {
		findBlock(addr);
	}

	/**
	 * update cache[setIndex] with tag, here needs the LRU strategy
	 * 
	 * @param setIndex
	 * @param tag
	 * @return position of this address
	 */
	private int update(int setIndex, long tag) {
		//TODO:: LRU
		Random r = new Random();
		cache[setIndex][r.nextInt(100) & 0x0007] = tag;
		return 0;
	}

	/**
	 * L2 is write-through, that is any write happens, will write to memory
	 * @param addr
	 */
	public void write(long addr) {
		findBlock(addr);
	}
	
	/**
	 * find a cache block for addr, if it is not existed in cache, need to fetch
	 * further from L1E and L2
	 * 
	 * @param addr
	 */
	private int findBlock(long addr) {
		// find cache by splitting addr into multiple segs
		int setIndex = (int) ((addr & 0x7FC0) >>> 6);
		long tag = (addr & 0x3FFFFFFFFFFF8000l) >>> 15;
		boolean found = false;
		// search for the set to check for existence
		for (int j = 0; j < 8; j++) {
			if (cache[setIndex][j] == tag) {
				found = true;
				break;
			}
		}
		if (!found) {
			//fetch from memory;
		}
		// after all, we need to update the entry
		int index = update(setIndex, tag);
		return index;
	}
	
}
