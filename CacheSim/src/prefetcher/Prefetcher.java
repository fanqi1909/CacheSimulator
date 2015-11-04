package prefetcher;

import java.util.ArrayList;

public interface Prefetcher {
	public ArrayList<Long> getPrefetchedAddress(long initAddr);
}
