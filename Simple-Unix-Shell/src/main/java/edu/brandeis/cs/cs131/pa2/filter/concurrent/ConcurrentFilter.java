/**
* The implementation for the concurrent filter class
* Known Bugs: none
*
* @author Ming-Shih Wang
* mingshihwang@brandeis.edu
* March 13, 2023
* COSI 131A PA2
*/
package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import edu.brandeis.cs.cs131.pa2.filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic
 * functionality of all filters. Each filter should extend this class and
 * implement functionality that is specific for this filter.
 */
public abstract class ConcurrentFilter extends Filter implements Runnable{
	/**
	 * The input pipe for this filter
	 */
	protected ConcurrentPipe input;
	/**
	 * The output pipe for this filter
	 */
	protected ConcurrentPipe output;
	
//	protected ConcurrentREPL repl = new ConcurrentREPL();

	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}

	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter) {
			ConcurrentFilter concurrentNext = (ConcurrentFilter) nextFilter;
			this.next = concurrentNext;
			concurrentNext.prev = this;
			if (this.output == null) {
				this.output = new ConcurrentPipe();
			}
			concurrentNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	
	public void run() {
		process();
	}
	
	/**
	 * Modified the process() to allow filter's to be run concurrently with Thread.start()
	 * Concurrently processes the input pipe and passes the result to the output pipe
	 */
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
			output.writePoisonPill();
		}catch(InterruptedException e) {
			// do nothing, the shell should continue running when a thread is killed or ended
			// all proceeding filter threads will be terminated as well through the ConcurrentREPL class. 
		}
	}

	/**
	 * Called by the {@link #process()} method for every encountered line in the
	 * input queue. It then performs the processing specific for each filter and
	 * returns the result. Each filter inheriting from this class must implement its
	 * own version of processLine() to take care of the filter-specific processing.
	 * 
	 * @param line the line got from the input queue
	 * @return the line after the filter-specific processing
	 */
	protected abstract String processLine(String line);

}
