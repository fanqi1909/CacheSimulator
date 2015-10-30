package caches;

import static conf.Constants.*;
import lru.Policy;

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
	private Policy[] policies;

	public L1DataCache(L2Cache l2, Class<?> lruClass) {
		l1e = new L1EvictedCache();
		this.l2 = l2;
		cache = new long[L1DSETS][8];
		policies = new Policy[L1DSETS];
		try {
			for (int i = 0; i < policies.length; i++) {
				// initialize access for each policies
				policies[i] = (Policy) lruClass.newInstance();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * find a cache block for addr, if it is not existed in cache, need to fetch
	 * further from L1E and L2
	 * 
	 * @param addr
	 */
	private int findBlock(int setIndex, long tag) {
		// search for the set to check for existence
		for (int j = 0; j < 8; j++) {
			if (cache[setIndex][j] == tag) {
				policies[setIndex].updateAt(j);
				return j;
			}
		}
		// we cannot find a block, thus needs to
		// do further look ups
		long address = getAddress(setIndex, tag);
		if (!l1e.lookup(address)) {
			l2.read(address);
		}
		int index = policies[setIndex].getNextIndex();
		if (cache[setIndex][index] != 0l) {
			long old_address = getAddress((setIndex << 6),
					cache[setIndex][index]);
			// put old address to victim cache
			l1e.insert(old_address);
		}
		// write back
		cache[setIndex][index] = tag;
		return index;
	}

	/**
	 * analyze address and get the SetIndex field, which is 6-bit long
	 * 
	 * @param addr
	 * @return
	 */
	private int getSetIndex(long addr) {
		return (int) ((addr & 0xFFFl) >> 6); // there is no overflow
	}

	/**
	 * analyze address and get the Tag field, which is 50-bit long
	 * 
	 * @param addr
	 * @return
	 */
	private long getTag(long addr) {
		return (addr & 0x3FFFFFFFFFFFF000l);
	}

	/**
	 * put up address based on Tag and SetIndex, the last 6-bit offset is set to
	 * be 0. We are safe to do so since those values does not play a role
	 * 
	 * @param setIndex
	 * @param tag
	 * @return
	 */
	private long getAddress(int setIndex, long tag) {
		return tag | (setIndex << 6);
	}

	/**
	 * CPU wish to access the addr. 
	 * We need to find an appropriate block based our policies.
	 * Note that, findBlock will override the address content of victims.
	 * @param addr
	 */
	public void access(long addr) {
		findBlock(getSetIndex(addr), getTag(addr));
	}

}
