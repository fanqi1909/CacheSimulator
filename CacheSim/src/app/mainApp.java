package app;

import cpu.MyCPU;

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
		MyCPU mycpu = new MyCPU();
		mycpu.Simulate("traces/ls.trace");
		mycpu.printStats();
	}
}
