package lru;

/**
 * This is a tree-based LRU implementation
 * Since the cache is 8-way associative, 
 * and each tree node is either 0 or 1, we can use
 * an integer to represent the tree.
 * A tree's node is 0 if its left is more recent, otherwise its right is more recent
 * @author a0048267
 *
 */
public class TreeBasedLRU implements Policy {
	/**
	 * ideally need to check whether size is a power of 2, but
	 * in this application, let's assume so. (8 mei pao le)
	 */
	private int[] tree;
	public TreeBasedLRU() {
		tree = new int[15];
	}
	
	/**
	 * return the Least Recently Used index from current LRU
	 * @return
	 */
	public int getNextIndex() {
		//traverse the tree
		int pos = 0;// from root;
		while(pos < 7) {
			int val = tree[pos];
			if(val == 1) { // means right is more recent
				//left
				pos = (pos+1) * 2-1;
			} else { // means left is more recent
				//right
				pos = (pos+1) * 2;
			}
		}
		//at this step, position is the leave node
		//but we need to update the tree before insert
		updateAt(pos);
		return pos-7; // deduct 7 because cache block is numbered from 0 to 7
	}
	
	
	
	public static void main(String[] args) {
		TreeBasedLRU tlru = new TreeBasedLRU();
		for(int i = 0; i < 10; i++) {
			System.out.println(tlru.getNextIndex());
		}
	}
	
	/**
	 * pos is recently accessed, so we need
	 * to update its value. (actually its parents' value)
	 * @param pos
	 */
	@Override
	public void updateAt(int pos) {
		while(pos != 0) {
			//find pos's parent
			int	parent = (pos-1)>>1;
			if(pos == (parent+1) * 2 ) {
				//right child
				tree[parent] = 1;
			} else {
				tree[parent] = 0;
			}
			pos = parent;
		}
	}
}
