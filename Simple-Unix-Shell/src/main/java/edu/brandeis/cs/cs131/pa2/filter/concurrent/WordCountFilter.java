/**
* The implementation for the concurrent wc command
* Known Bugs: none
*
* @author Ming-Shih Wang
* mingshihwang@brandeis.edu
* March 13, 2023
* COSI 131A PA2
*/
package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements wc command - overrides necessary behavior of ConcurrentFilter
 * 
 */
public class WordCountFilter extends ConcurrentFilter {

	/**
	 * word count in input - words are strings separated by space in the input
	 */
	private int wordCount;

	/**
	 * character count in input - includes ws
	 */
	private int charCount;

	/**
	 * line count in input
	 */
	private int lineCount;

	/**
	 * Constructs a wc filter.
	 */
	public WordCountFilter() {
		super();
		wordCount = 0;
		charCount = 0;
		lineCount = 0;
	}

	/**
	 * Modified the process() to allow filter's to be run concurrently with Thread.start()
	 * Overrides {@link ConcurrentFilter#process()} by computing the word count,
	 * line count, and character count then adding the string with line count + " "
	 * + word count + " " + character count to the output queue
	 */
	@Override
	public void process() {
		try {
			String concurrentInput = input.readAndWait();
			while (concurrentInput != null) {
				String line = concurrentInput;
				processLine(line);
				concurrentInput = input.readAndWait();
			}
			output.write(lineCount + " " + wordCount + " " + charCount);
			output.writePoisonPill();
		}catch(InterruptedException e) {
			// do nothing, the shell should continue receive user's input after being killed or terminated
			// all proceeding filter threads will be terminated as well through the ConcurrentREPL class. 
		}
		
	}

	/**
	 * Overrides ConcurrentFilter.processLine() - updates the line, word, and
	 * character counts from the current input line
	 */
	@Override
	protected String processLine(String line) {
		lineCount++;
		wordCount += line.split(" ").length;
		charCount += line.length();
		return null;
	}

}
