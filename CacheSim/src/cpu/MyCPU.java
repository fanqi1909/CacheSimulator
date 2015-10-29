package cpu;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import conf.Constants;
import caches.L1DataCache;
import caches.L1InstructionCache;
import caches.L2Cache;

/**
 * simulate the CPU process
 * 
 * @author a0048267
 *
 */
public class MyCPU {
	//we do not have direct access to L2 caches 
	private L1InstructionCache l1in;
	private L1DataCache l1d;
	//statistics variables

	
	public MyCPU() {
		L2Cache l2 = new L2Cache();
		//ensure that l1s get the same l2
		l1in = new L1InstructionCache(l2);
		l1d = new L1DataCache(l2);
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
				process(instruction);
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
	private void process(long instruction) {
		//decode instruction
		long lasttwo = (instruction & 0xC000000000000000l) >>> 62;
		if(lasttwo == 0) { //instruction fetch
		 	l1in.fetch(instruction);
		} else if(lasttwo == 1) { //load 
			l1d.read(instruction);
		} else if(lasttwo == 2) { //write 
			l1d.write(instruction);
		}
	}

	public void printStats() {
		
	}
}
