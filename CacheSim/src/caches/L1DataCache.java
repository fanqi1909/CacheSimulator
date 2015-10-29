package caches;

import static conf.Constants.*;

/**
 * L2 instruction cache is 32Kb, block size 64 byte, 8-way set associative write
 * back
 * 
 * in L2 cache, each memory address is of the following form:
 * |--2-bit--op|-----50
 * -bit----tag|------6-bit--set-index---|----6-bit-offset---| 63 62 61 12 11 6 5
 * 0
 * 
 * @author a0048267
 * 
 */
public class L1DataCache {
	private L1EvictedCache l1e;
	private L2Cache l2;
	private long[][] cache;

	public L1DataCache(L2Cache l2) {
		l1e = new L1EvictedCache();
		this.l2 = l2;
		cache = new long[L1DSETS][8];
	}

	/**
	 * find a cache block for addr, if it is not existed in cache, need to fetch
	 * further from L1E and L2
	 * 
	 * @param addr
	 */
	private int findBlock(long addr) {
		// find cache by splitting addr into multiple segs
		int setIndex = (int) ((addr & 0xFFFl) >> 6); // there is no overflow
		long tag = (addr & 0x3FFFFFFFFFFFF000l);
		boolean found = false;
	
		// search for the set to check for existence
		for (int j = 0; j < 8; j++) {
			if (cache[setIndex][j] == tag) {
				found = true;
				break;
			}
		}
		if (!found) {
			// look up at the evicted
			if(!l1e.lookup(addr)) {
				l2.read(addr);
			}
		}
		// after all, we need to update the entry
		int index = update(setIndex, tag);
		return index;
	}

	public void read(long addr) {
		findBlock(addr);
	}

	/**
	 * update the cache[setIndex] with Tag,
	 * if Tag does not exists, insert it in,
	 * @param setIndex
	 * @param tag
	 * @return index, the position of this address
	 */
	private int update(int setIndex, long tag) {
		return 0;
	}

	/**
	 * since it is a write back, if the data is inside, the process is the same
	 * as read
	 * 
	 * @param addr
	 */
	public void write(long addr) {
		 findBlock(addr);
		 return;
	}
}
