package caches;

import static conf.Constants.*;
import lru.Policy;

/**
 * L1 instruction cache is 32Kb,  block size 64 byte, 8-way set associative
 * note that L1 cache do not have the write option, that is not dirty bit exists
 * 
 * in L1 cache, each memory address is of the following form:
 * |--2-bit--op|-----50-bit----tag|------6-bit--set-index---|----6-bit-offset---|
 * 63		 62 61              12 11                      6 5                  0
 * 
 * 
 */

public class L1InstructionCache {
	private L2Cache myL2;
	
	private long[][] cache;
	private Policy[] policies;
	
	public L1InstructionCache(L2Cache l2, Class<?> policyClass) {
		myL2 = l2;
		cache = new long[L1INSETS][8];
		policies = new Policy[L1INSETS]; //each policy for one cacheline
		try {
			for (int i = 0; i < policies.length; i++) {
				policies[i] = (Policy) policyClass.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
 

	public void fetch(long addr) {
		//find cache by splitting addr into multiple segs
		int setIndex = (int) ((addr & 0xFFFl)>>6); // there is no overflow
		long tag = (addr & 0x3FFFFFFFFFFFF000l);
		boolean found = false;
		//search for the set to check for existence
		for(int j = 0; j < 8; j++) {
			if(cache[setIndex][j] == tag) {
				found = true;
				//update this entry in LRU
				policies[setIndex].updateAt(j);
				break;
			}
		}
		if(!found) {
			//too bad, the same address is passed to L2
			myL2.read(addr);
			//after all, we need to update the entry
			int nextIndex = policies[setIndex].getNextIndex();
			cache[setIndex][nextIndex] = tag;	
		}
	}
}
