package prefetcher;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * We have an reference prediction table to predict the
 * addresses to be fetched from main memory
 * The original design in  Chen95 was for instruction cache
 * Here we use it in L2 data cache
 * Chen95 used instruction address as tag and use operand for stride testing,
 * However, L2 data cache do not have the instruction address.  The
 * trick we use is to compute tags of a given address. Using that tag as the index. 
 * 
 * @author fanqi_000
 *
 */
public class TableBasedPrefetcher implements Prefetcher{
	private HashMap<Long, Integer> rpt_index;
	private Tuple[] rpt;
	public TableBasedPrefetcher() {
		rpt_index = new HashMap<>(); // the index is used for fast retrieve tuples
		rpt = new Tuple[16]; //16 entries for rpt 
		for(int i = 0; i <rpt.length; i++) {
			rpt[i] = new Tuple();
		}
	}
	
	@Override
	public ArrayList<Long> getPrefetchedAddress(long initAddr) {
		ArrayList<Long> result = new ArrayList<>();
		result.add(initAddr); //has to include this address
		long tag = initAddr & Tuple.TAG_MASK;
		
		//query rpt
		if(!rpt_index.containsKey(tag)) {
			//creat a new entry
			int index = 0;
			for(int i = 0; i < 16; i++) {
				if(rpt[i].isEmpty()) {
					index = i;
					break;
				}
			}
			rpt_index.put(tag, index);
			rpt[index].setState(Tuple.INIT); //init state
			rpt[index].setTag(tag);
			rpt[index].setPrev_addr(initAddr);
		} else {
			int index =rpt_index.get(tag);
			boolean correct =  (rpt[index].getPrev_addr() + rpt[index].getStride())
							== initAddr;
			//transition as shown in Chen95 paper
			if(!correct){
				if(rpt[index].getState() == Tuple.INIT) {
					rpt[index].setStride((int) (initAddr - rpt[index].getPrev_addr()));
					rpt[index].setPrev_addr(initAddr);
					rpt[index].setState(Tuple.TRANS);
				} else if(rpt[index].getState() == Tuple.STEADY){
					//move back to initialization
					rpt[index].setPrev_addr(initAddr);
					rpt[index].setState(Tuple.INIT);
				}else if(rpt[index].getState() == Tuple.TRANS){
					//detection of irregular pattern
					rpt[index].setStride((int) (initAddr-rpt[index].getPrev_addr()));
					rpt[index].setPrev_addr(initAddr);
					rpt[index].setState(Tuple.NOP);
				} else { // state == NOP
					rpt[index].setStride((int) (initAddr-rpt[index].getPrev_addr()));
					rpt[index].setPrev_addr(initAddr);
				}
			} else {
				if(rpt[index].getState() != Tuple.NOP) {
					rpt[index].setPrev_addr(initAddr);
					rpt[index].setState(Tuple.STEADY);
				} else {
					rpt[index].setPrev_addr(initAddr);
					rpt[index].setState(Tuple.TRANS);
				}
			}
		}
		//check status
		int index = rpt_index.get(tag);
		if(rpt[index].getState() != Tuple.NOP) {
			//prefetch add, add+ s, add+ s*2,... , add+s*(PF_SIZE-1)
			int stride = rpt[index].getState();
			if(stride != 0) {
				for(int i = 1; i < Tuple.PF_SIZE; i++) {
					long new_add = initAddr + i * stride;
					result.add(new_add);
				}
			}
		}
		return result;
	}

}

class Tuple{ 
	public static final long TAG_MASK=0x3FFFFFFFFFFFF000l; // we pick the same tag as in L1DataCache

	public static final int PF_SIZE = 8; //number of prefeches
	
	public static final int INIT = 0; 
	public static final int TRANS = 1;
	public static final int STEADY =2;
	public static final int NOP = 3;
	boolean isEmpty = true;
	//corresponding to the address access
	private long tag; // this should corresponding key of rpt_index, we deliberately make the duplication
	//the last address that is accessed
	private long prev_addr;
	//the difference between the last two address that were generated
	private int stride;
	//a two-bit encoding of the past history
	//0: initial, set the first entry in RPT , reset after an incorrect prediction from steady state
	//1: transient, system unsure about previous prediction, stride = prev_addr - tag
	//2: steady, predication is stable
	//3: no predication , disable the prefetching
	private int state;
	
	public Tuple() {
		clear();
	}
	
	public long getTag() {
		return tag;
	}

	public void setTag(long tag) {
		isEmpty = false;
		this.tag = tag;
	}

	public long getPrev_addr() {
		return prev_addr;
	}

	public void setPrev_addr(long prev_addr) {
		isEmpty = false;
		this.prev_addr = prev_addr;
	}

	public int getStride() {
		isEmpty = false;
		return stride;
	}

	public void setStride(int stride) {
		isEmpty = false;
		this.stride = stride;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		isEmpty = false;
		this.state = state;
	}

	public void clear(){
		tag = 0; prev_addr = 0; stride = 0; state = 0;
		isEmpty = true;
	}
	public boolean isEmpty() {
		return isEmpty;
	}
}