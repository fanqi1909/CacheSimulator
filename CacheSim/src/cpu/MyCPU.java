package cpu;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import lru.BitBasedLRU;
import lru.TreeBasedLRU;
import prefetcher.NeverPrefetcher;
import prefetcher.TableBasedPrefetcher;
import conf.Constants;
import caches.L1DataCache;
import caches.L1InstructionCache;
import caches.L2Cache;

/**
 * simulate the CPU process
 * 
 * @author e0001421
 *
 */
public class MyCPU {
	//we do not have direct access to L2 caches 
	private L1InstructionCache l1in;
	private L1DataCache l1d;
	private int CountTotalInstruction;
	private int CountTotalInstructionFetch;
	private int CountTotalDataLoad;
	private int CountTotalDataStore;
	
	private int CountInstructionFetchHitL1; //hit at the L1 cache 
	private int CountInstructionFetchHitL2; //hit at the L2 cache 
	private int CountInstructionFetchHitMem; //hit at the L2 cache 
	
	private int CountDataLoadHitL1;
	private int CountDataLoadHitVic;
	private int CountDataLoadHitL2;
	private int CountDataLoadHitMem;

	private int CountDataStoreHitL1;
	private int CountDataStoreHitVic;
	private int CountDataStoreHitL2;
	private int CountDataStoreHitMem;
	
	public MyCPU() {
		L2Cache l2 = new L2Cache(BitBasedLRU.class, TableBasedPrefetcher.class);
		//ensure that l1s get the same l2
		l1in = new L1InstructionCache(l2, BitBasedLRU.class);
		l1d = new L1DataCache(l2, BitBasedLRU.class);
		
		CountTotalInstruction = 0;
		CountTotalInstructionFetch = 0;
		CountTotalDataLoad = 0;
		CountTotalDataStore = 0;
		
		CountInstructionFetchHitL1 = 0; //hit at the L1 cache 
		CountInstructionFetchHitL2 = 0; //hit at the L2 cache 
		CountInstructionFetchHitMem = 0; //hit at the L2 cache 
		
		CountDataLoadHitL1 = 0;
		CountDataLoadHitVic = 0;
		CountDataLoadHitL2 = 0;
		CountDataLoadHitMem = 0;

		CountDataStoreHitL1 = 0;
		CountDataStoreHitVic = 0;
		CountDataStoreHitL2 = 0;
		CountDataStoreHitMem = 0;
	}
	
	public void Simulate(String inputFile) {
		ByteBuffer bbf = null;
		try{
			File input = new File(inputFile);
			FileInputStream fis = new FileInputStream(input);
			byte[] buffer = new byte[Constants.ADDRESS_LENGTH];
			while(fis.read(buffer) != -1) {
				bbf = ByteBuffer.wrap(buffer);
				bbf.order(ByteOrder.LITTLE_ENDIAN);
				long instruction = bbf.getLong();
				int result = process(instruction);
				switch(result){
				case 1: 
					CountInstructionFetchHitL1++;
					break;
				case 3:
					CountInstructionFetchHitL2++;
					break;
				case 4:
					CountInstructionFetchHitMem++;
					break;
				case 11:
					CountDataLoadHitL1++;
					break;
				case 12:
					CountDataLoadHitVic++;
					break;
				case 13:
					CountDataLoadHitL2++;
					break;
				case 14:
					CountDataLoadHitMem++;
					break;
				case 21:
					CountDataStoreHitL1++;
					break;
				case 22:
					CountDataStoreHitVic++;
					break;
				case 23:
					CountDataStoreHitL2++;
					break;
				case 24:
					CountDataStoreHitMem++;
					break;
				default:
					break;
				
				}
			}
			fis.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * decode address to perform according tasks
	 * @param addr
	 */
	private int process(long instruction) {
		//decode instruction
		long lasttwo = (instruction & 0xC000000000000000l) >>> 62;
		if(lasttwo == 0) { //instruction fetch
		 	return l1in.fetch(instruction);
		} else if(lasttwo == 1) { //load 
			return l1d.access(instruction) + 10;
		} else if(lasttwo == 2) { //write 
			return l1d.access(instruction) + 20;
		}
		return 0;
	}

	/**
	 * need to collect relevant statistics
	 */
	public void printStats() {

		CountTotalInstructionFetch = CountInstructionFetchHitL1 + CountInstructionFetchHitL2 + CountInstructionFetchHitMem;
		CountTotalDataLoad = CountDataLoadHitL1 + CountDataLoadHitVic + CountDataLoadHitL2 + CountDataLoadHitMem;
		CountTotalDataStore = CountDataStoreHitL1 + CountDataStoreHitVic + CountDataStoreHitL2 + CountDataStoreHitMem;
		CountTotalInstruction = CountTotalInstructionFetch + CountTotalDataLoad + CountTotalDataStore;
		
		int TotalHitL1 = CountInstructionFetchHitL1 + CountDataLoadHitL1
				+ CountDataStoreHitL1;
		int TotalHitVic = CountDataLoadHitVic + CountDataStoreHitVic;
		int TotalHitL2 = CountDataLoadHitL2 + CountDataStoreHitL2 + CountInstructionFetchHitL2;
		int TotalHitMem = CountInstructionFetchHitMem + CountDataLoadHitMem + CountDataStoreHitMem;
		
		System.out.println("*******************************SUMMARY******************************");
		System.out.println("Total number of instructions: " + CountTotalInstruction);
		System.out.println("Total number of data load: " + CountTotalDataLoad) ;
		System.out.println("Total number of data store: " + CountTotalDataStore);
		System.out.println("Total number of instruction fetch: " + CountTotalInstructionFetch);
		System.out.println("\n********SUMMARY AT HARDWARE LEVEL*********");
		System.out.println("Total number of hit at level 1: " + TotalHitL1);
		System.out.println("Total number of hit victim cache: " + TotalHitVic);
		System.out.println("Total number of hit at level 2: " + TotalHitL2);
		System.out.println("Total number of hit memory: " + TotalHitMem);
		System.out.println("\n********INSTRUCTION FETCH*********");
		System.out.println("Total number of hit at level 1: " + CountInstructionFetchHitL1+ "   " + String.format( "%.3f", (double)CountInstructionFetchHitL1/CountTotalInstructionFetch));
		System.out.println("Total number of hit at level 2: " + CountInstructionFetchHitL2+ "   " + String.format( "%.3f", (double)CountInstructionFetchHitL2/CountTotalInstructionFetch));
		System.out.println("Total number of hit memory: " + CountInstructionFetchHitMem+ "   " + String.format( "%.3f", (double)CountInstructionFetchHitMem/CountTotalInstructionFetch));
		System.out.println("\n********DATA LOAD*********");
		System.out.println("Total number of hit at level 1: " + CountDataLoadHitL1+ "   " + String.format( "%.3f", (double)CountDataLoadHitL1/CountTotalDataLoad));
		System.out.println("Total number of hit victim cache: " + CountDataLoadHitVic+ "   " + String.format( "%.3f", (double)CountDataLoadHitVic/CountTotalDataLoad));
		System.out.println("Total number of hit at level 2: " + CountDataLoadHitL2+ "   " + String.format( "%.3f", (double)CountDataLoadHitL2/CountTotalDataLoad));
		System.out.println("Total number of hit memory: " + CountDataLoadHitMem+ "   " + String.format( "%.3f", (double)CountDataLoadHitMem/CountTotalDataLoad));
		System.out.println("\n********DATA STORE*********");
		System.out.println("Total number of hit at level 1: " + CountDataStoreHitL1+ "   " + String.format( "%.3f", (double)CountDataStoreHitL1/CountTotalDataStore));
		System.out.println("Total number of hit victim cache: " + CountDataStoreHitVic+ "   " + String.format( "%.3f", (double)CountDataStoreHitVic/CountTotalDataStore));
		System.out.println("Total number of hit at level 2: " + CountDataStoreHitL2+ "   " + String.format( "%.3f", (double)CountDataStoreHitL2/CountTotalDataStore));
		System.out.println("Total number of hit memory: " + CountDataStoreHitMem+ "   " + String.format( "%.3f", (double)CountDataStoreHitMem/CountTotalDataStore));
		
	}
}
