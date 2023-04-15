/**
* The implementation for the concurrent ls command
* Known Bugs: none
*
* @author Ming-Shih Wang
* mingshihwang@brandeis.edu
* March 13, 2023
* COSI 131A PA2
*/
package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.io.File;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements ls command - overrides necessary behavior of ConcurrentFilter
 * 
 *
 */
public class ListFilter extends ConcurrentFilter {

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs an ListFilter from an exit command
	 * 
	 * @param cmd - exit command, will be "ls" or "ls" surrounded by whitespace
	 */
	public ListFilter(String cmd) {
		super();
		command = cmd;
	}

	/**
	 * Overrides ConcurrentFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		return null;
	}

	/**
	 * Modified the process() to allow filter's to be run concurrently with Thread.start()
	 * Overrides {@link ConcurrentFilter#process()} to add the files located in
	 * the current working directory to the output queue.
	 */
	@Override
	public void process() {
		File cwd = new File(CurrentWorkingDirectory.get());
		File[] files = cwd.listFiles();
		try {
			for (File f : files) {
				this.output.writeAndWait(f.getName());
			}
			this.output.writePoisonPill();
		}catch(InterruptedException e) {
			// do nothing, the shell should continue receive user's input after being killed or terminated
			// all proceeding filter threads will be terminated as well through the ConcurrentREPL class. 
		}

	}

	/**
	 * Overrides ConcurrentFilter.setPrevFilter() to not allow a
	 * {@link Filter} to be placed before {@link ListFilter} objects.
	 * 
	 * @throws InvalidCommandException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		throw new InvalidCommandException(Message.CANNOT_HAVE_INPUT.with_parameter(command));
	}

}
