package prefetcher;

import java.util.ArrayList;

/**
 * used as a baseline to check whether
 * prefetcher works or not
 * @author fanqi_000
 *
 */
public class NeverPrefetcher implements Prefetcher {

	@Override
	public ArrayList<Long> getPrefetchedAddress(long initAddr) {
		ArrayList<Long> result = new ArrayList<>();
		result.add(initAddr);
		return result;
	}

}
