package app;

import cpu.MyCPU;
import conf.Constants;
/**
 * entry point of the entire simulator
 * @author e0001421
 *
 */
public class mainApp {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyCPU mycpu = new MyCPU(true, true);
		mycpu.Simulate(Constants.ls_trace_file);
		mycpu.printStats();
	}
}
