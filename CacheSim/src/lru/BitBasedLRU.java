package lru;

/**
 * This is a bit-based LRU implementation
 * For each cash line, there are 8 bits standing for the status for each unit
 * When accessed, set the bit to 1. And if all others are 1 too, then set the rest to 0
 * When missed, find the first index that = 0, replace the value in this unit with the new one
 * @author e0001421
 *
 */
public class BitBasedLRU implements Policy {
	/**
	 * ideally need to check whether size is a power of 2, but
	 * in this application, let's assume so -> 8
	 */
	private int bits;
	public BitBasedLRU() {
		bits = 0;
	}
	
	public BitBasedLRU(int bits){
		
		this.bits = bits;
	}
	
	private int findFirstZeroBit(){
	
		if((bits & 0x01) == 0) {
			return 0;
		}
		if((bits & 0x02) == 0) {
			return 1;
		}
		if((bits & 0x04) == 0) {
			return 2;
		}
		if((bits & 0x08) == 0) {
			return 3;
		}
		if((bits & 0x10) == 0) {
			return 4;
		}
		if((bits & 0x20) == 0) {
			return 5;
		}
		if((bits & 0x40) == 0) {
			return 6;
		}
		if((bits & 0x80) == 0) {
			return 7;
		}
		return -1;
	}
	
	private void print() {
		System.out.println(bits);
	}
	
	/**
	 * return the Least Recently Used index from current LRU
	 * @return
	 */
	@Override
	public int getNextIndex() {

		int pos = findFirstZeroBit();
		updateAt(pos);
		return pos;
	}
	
	public static void main(String[] args) {
		BitBasedLRU blru = new BitBasedLRU(15);
		System.out.println(blru.getNextIndex());
		blru.print();
	}
	
	/**
	 * pos is recently accessed, so we need
	 * to update its value and potentially rest of the values
	 * @param pos
	 */
	@Override
	public void updateAt(int pos) {
		bits = bits | (1<<pos);
		if(bits == 0xFF) {
			bits = (1<<pos);
		}
	}
}
