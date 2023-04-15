/**
* The implementation for the concurrent > command
* Known Bugs: none
*
* @author Ming-Shih Wang
* mingshihwang@brandeis.edu
* March 13, 2023
* COSI 131A PA2
*/
package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements redirection as a {@link ConcurrentFilter} - overrides necessary
 * behavior of ConcurrentFilter
 * 
 * @author Chami Lamelas
 *
 */
public class RedirectFilter extends ConcurrentFilter {

	/**
	 * destination of redirection
	 */
	private String dest;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * writer for writing - set in process(), leave as null till then
	 */
	private PrintWriter printWriter;

	/**
	 * Constructs a RedirectFilter given a >.
	 * 
	 * @param cmd cmd is guaranteed to either be ">" or ">" followed by a space.
	 * @throws InvalidCommandException if a file parameter was not provided
	 */
	public RedirectFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setNextFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just ">" =>
		// > needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new InvalidCommandException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, filename will be trimmed string after space
		String relativeDest = cmd.substring(spaceIdx + 1).trim();

		// set redirection destination as cwd joined with relative destination file
		dest = CurrentWorkingDirectory.get() + CurrentWorkingDirectory.getPathSeparator() + relativeDest;

		// check if the destination file exists, if so delete it b/c > overwrites the
		// destination file if one exists
		File destFile = new File(dest);
		if (destFile.isFile()) {
			destFile.delete();
		}
	}

	/**
	 * Modified the process() to allow filter's to be run concurrently with Thread.start()
	 * Overrides ConcurrentFilter.run to close write stream
	 */
	@Override
	public void process() {
		try {
			
			printWriter = new PrintWriter(new FileWriter(new File(dest)));
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
			printWriter.close();
		} catch (IOException e) {
			// do nothing we know file exists
		}

	}

	/**
	 * Overrides ConcurrentFilter.processLine() to just write the line to the
	 * destination file. Returns null so {@link ConcurrentFilter#process()} doesn't
	 * add anything to the output.
	 */
	@Override
	protected String processLine(String line) {
		printWriter.println(line);
		return null;
	}

	/**
	 * Overrides ConcurrentFilter.setPrevFilter() to not allow a {@link Filter} to
	 * be placed after {@link RedirectFilter} objects.
	 * 
	 * @throws InvalidCommandException - always
	 */
	@Override
	public void setNextFilter(Filter nextFilter) {
		throw new InvalidCommandException(Message.CANNOT_HAVE_OUTPUT.with_parameter(command));
	}

}
