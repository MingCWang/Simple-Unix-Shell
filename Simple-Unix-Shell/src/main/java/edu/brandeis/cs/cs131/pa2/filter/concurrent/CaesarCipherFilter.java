/**
* The implementation for the concurrent CC filter
* Known Bugs: none
*
* @author Ming-Shih Wang
* mingshihwang@brandeis.edu
* March 13, 2023
* COSI 131A PA2
*/
package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements cc command - includes parsing cc command by overriding
 * necessary behavior of SequentialFilter.
 *
 */
public class CaesarCipherFilter extends ConcurrentFilter {

	private int key;

	public CaesarCipherFilter(String cmd) {
		super();

		// find index of space, if there isn't a space that means we got just "cc" =>
		// cc needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new InvalidCommandException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, key will be trimmed int after space
		key = Integer.parseInt(cmd.substring(spaceIdx + 1).trim());
	}

	/**
	 * Modified the process() to allow filter's to be run concurrently with Thread.start()
	 * Overrides {@link ConcurrentFilter#process()} to push lines of input that are encrypted with caeser cipher to the output
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
			output.writePoisonPill();
		}catch(InterruptedException e) {
			// do nothing, the shell should continue receive user's input after being killed or terminated
			// all proceeding filter threads will be terminated as well through the ConcurrentREPL class. 
		}
	}
	
	@Override
	protected String processLine(String line) {
		char[] output = new char[line.length()];

		for (int i = 0; i < line.length(); i++) {

			// Whether or not the current character is A-Z
			boolean isUpper = false;
			char c = line.charAt(i);

			// If c is upper case, marks that we've seen it and set c to lower case
			// (simplifies logic later)
			if (Character.isUpperCase(c)) {
				isUpper = true;
				c = Character.toLowerCase(c);
			}

			// If lower case version of character is not in a-z (that means c isn't in a-z
			// or A-Z). Thus, we keep unchanged version of character in output.
			if (c < 'a' || c > 'z') {
				output[i] = c;
				continue;
			}

			// At this point, c is in a-z. Map c to 0-25. Perform the shift to get changed c
			// in 0-25. Map changed character back to a-z.
			c = (char) ((((c - 'a') + key) % 26) + 'a');

			// If character was A-Z (and shifted above to something else in A-Z), convert it
			// back from a-z before storing in the output.
			if (isUpper) {
				c = Character.toUpperCase(c);
			}
			output[i] = c;
		}

		return new String(output);
	}

}
