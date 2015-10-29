package app;

import cpu.MyCPU;

/**
 * entry point of the entire simulator
 * @author a0048267
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
