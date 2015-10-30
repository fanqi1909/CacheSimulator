package caches;

import static conf.Constants.*;

import lru.Policy;

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
	private Policy[] policies;
	public L2Cache(Class<?> policyName) {
		cache = new long[L2SETS][8];
		policies = new Policy[L2SETS];
		try {
			for(int i = 0; i < policies.length; i++) {
				policies[i] = (Policy) policyName.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
		// search for the set to check for existence
		for (int j = 0; j < 8; j++) {
			if (cache[setIndex][j] == tag) {
				policies[setIndex].updateAt(j);
				return j;
			}
		}
		//fetch from memory;
		//insert to cache;
		int index = policies[setIndex].getNextIndex();
		cache[setIndex][index] = tag;
		// after all, we need to update the entry
		return index;
	}
	
}
