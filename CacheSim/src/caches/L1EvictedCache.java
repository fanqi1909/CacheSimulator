package caches;

import java.util.Random;
import static conf.Constants.*;
/**
 * victim cache is in L1, fully-associative
 * If a data read/write succeeds, the data is moved back to L1 data cache
 * but there is no policy class for L1E
 * @author a0048267
 *
 */
public class L1EvictedCache {

	private long [] cache; 
	private Random r;
	public L1EvictedCache() {
		cache = new long[L1ESLOT];
		r = new Random();
	}
	
	/**
	 * look up is requested for addr, 
	 * if we have this address, then just clear it
	 * if don't, return false;
	 * @param addr
	 * @return
	 */
	public boolean lookup(long addr) {
		boolean found = false;
		for(int i = 0; i < cache.length; i++) {
			if(cache[i] == addr) {
				cache[i] = 0;
				found = true;
				break;
			}
		}
		return found;
	}
	
	public void insert(long addr) {
		boolean inserted = false;
		for(int i = 0; i < cache.length; i++) {
			if(cache[i] == 0) {
				cache[i] = addr;
				inserted = true;
				break;
			}
		}
		if(!inserted) {
			//cache is full;
			cache[(r.nextInt(100) & 0xF)] = addr;
		}
	}
}
