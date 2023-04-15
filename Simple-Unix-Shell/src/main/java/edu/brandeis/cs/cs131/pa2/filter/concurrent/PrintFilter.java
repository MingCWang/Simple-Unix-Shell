/**
* The implementation for stdout
* Known Bugs: none
*
* @author Ming-Shih Wang
* mingshihwang@brandeis.edu
* March 13, 2023
* COSI 131A PA2
*/
package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements printing as a {@link ConcurrentFilter} - overrides necessary
 * behavior of ConcurrentFilter
 *
 */
public class PrintFilter extends ConcurrentFilter {

	
	
	/**
	 * Modified the process() to allow filter's to be run concurrently with Thread.start()
	 * Overrides {@link ConcurrentFilter#process()} to push lines of input to stdout
	 */
	@Override
	public void process() {
		try {
			String concurrentInput = input.readAndWait();
			while (concurrentInput != null) {
				String line = concurrentInput;
				String processedLine = processLine(line);
				if (processedLine != null) {
					output.writeAndWait(processedLine);
				}
				concurrentInput = input.readAndWait();
			}
		}catch(InterruptedException e) {
			// do nothing, the shell should continue receive user's input after being killed or terminated
			// all proceeding filter threads will be terminated as well through the ConcurrentREPL class. 
		}
	}
	
	/**
	 * Overrides ConcurrentFilter.processLine() to just print the line to stdout.
	 */
	@Override
	protected String processLine(String line) {

		System.out.println(line);
		return null;
	}

}
