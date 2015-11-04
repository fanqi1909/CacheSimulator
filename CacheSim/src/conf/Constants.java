package conf;

public class Constants {

	public static final int ADDRESS_LENGTH = 8; //unit is in byte, so it is 8*8-64 bite
	
	public static final int MEM_OFFSET = 6; // log2(64) = 6, 0-5 is used for 
	
	public static final int L1INSLOT = 512; // 32KB / 64 byte
	public static final int L1INSIZE = 64; // 64 byte per slot
	public static final int L1INSETS = 64; // 512/8 = 64 sets
	public static final int L1INSETSIZE = 6; // bit, log2(64)
	
	
	public static final int L2SLOT = 4096; // 256KB/64 byte;
	public static final int L2SIZE = 64;
	public static final int L2SETS = 512; // 4096/8 = 512 sets
	public static final int L2SETSIZE = 9; // bit,log2(512)
	
	
	public static final int L1DSLOT = 512; // 32KB / 64 byte
	public static final int L1DSIZE = 64; // 64 byte per slot
	public static final int L1DSETS = 64; // 512/8 = 64 sets
	public static final int L1DSETSIZE = 6; // bit, log2(64)
	
	
	public static final int L1ESLOT = 8; //8 slots,  each 64 byte
	
	public static final String gz_trace_file = "/Users/cuixiang/Desktop/NUS/2015_16_sem1/CS5222/homework/gz.trace";

	public static final String ls_trace_file = "/Users/cuixiang/Desktop/NUS/2015_16_sem1/CS5222/homework/ls.trace";
}