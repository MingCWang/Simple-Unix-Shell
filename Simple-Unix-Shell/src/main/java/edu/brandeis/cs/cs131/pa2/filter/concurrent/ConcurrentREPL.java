/**
* The implementation for the concurrent REPL process 
* Known Bugs: none
*
* @author Ming-Shih Wang
* mingshihwang@brandeis.edu
* March 13, 2023
* COSI 131A PA2
*/
package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 */
public class ConcurrentREPL {
	
//	private static int counter = 0;
	/**
	 * pipe string
	 */
	static final String PIPE = "|";

	/**
	 * redirect string
	 */
	static final String REDIRECT = ">";

	/**
	 * The main method that will execute the REPL loop
	 * 
	 * @param args not used
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		Scanner consoleReader = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		
		// create hashmap to store the threads that are still alive in the background
		Map<Integer, ArrayList<Object>> repl_jobs = new HashMap<Integer, ArrayList<Object>>();
		// numbering variable for each background jobs 
		int num = 1;
		
		while (true) {
			System.out.print(Message.NEWCOMMAND);

			// read user command, if its just whitespace, skip to next command
			String cmd = consoleReader.nextLine();
			if (cmd.trim().isEmpty()) {
				continue;
			}
			//check if the command should be executed in the background
			boolean isBackground = false;
			if ((cmd.substring(cmd.length() - 2).equals(" &"))){
				isBackground = true;
				cmd = cmd.substring(0, cmd.length() - 2);
			}
			// check if the user entered exit, repl_jobs, or kill and executes the respective commands
			if (cmd.trim().equals("exit")) {
				break;
			}else if (cmd.trim().equals("repl_jobs")) {
				// creates an iterator to loop through the background jobs in the hashmap
				Iterator<Map.Entry<Integer,ArrayList<Object>>> entries = repl_jobs.entrySet().iterator();
				while(entries.hasNext()){
					Map.Entry<Integer,ArrayList<Object>> entry = entries.next();
					ArrayList<Thread> backgroundThreads = (ArrayList<Thread>)entry.getValue().get(0);
					Thread backgroundThread = (Thread)backgroundThreads.get(backgroundThreads.size() - 1);
					Integer backgroundThreadID = (Integer)entry.getKey();
					String backgroundThreadCMD = (String)entry.getValue().get(1);
					// output the commands that are still active
					if (backgroundThread.isAlive()) {
						System.out.println("\t" + backgroundThreadID + ". " + backgroundThreadCMD);
					}else {
						entries.remove();
					}
				}
			}else if(cmd.trim().split("\\s+")[0].equals("kill")) {
				// convert user's input of job id into integer
				Integer jobNum = Integer.valueOf(cmd.trim().split("\\s+")[1]);
				// creates an iterator to loop through the background jobs in the hashmap
				Iterator<Map.Entry<Integer,ArrayList<Object>>> entries = repl_jobs.entrySet().iterator();
				
				boolean jobExist = false;
				// kill the command that matches the inputted job id
				while(entries.hasNext()){
					Map.Entry<Integer,ArrayList<Object>> entry = entries.next();
					ArrayList<Thread> backgroundThreads = (ArrayList<Thread>)entry.getValue().get(0);
					Integer backgroundThreadID = (Integer)entry.getKey();
					if (backgroundThreadID == jobNum) {
						// terminate all threads for the command that has been killed 
						for (Thread thread : backgroundThreads) {
							thread.interrupt();
						}
						entries.remove();
						jobExist = true;
						break;
					}
				}
				// if no ID matches the user's input, return error message
				if(!jobExist) {
					System.out.println("no job id matches : " + jobNum);
				}
			}else{ // if the command is neither repl_jobs, kill, or exit, process the command below.
				try {
					// parse command into sub commands, then into Filters, add final PrintFilter if
					// necessary, and link them together - this can throw IAE so surround in
					// try-catch so appropriate Message is printed (will be the message of the IAE)
					List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd);
					// create an ArrayList for each process to store the threads
					ArrayList<Thread> threads = new ArrayList<Thread>();
					
					// call start() on each of the filters to have them execute
					for (ConcurrentFilter filter : filters) {
						// create threads for each filter and start them and store them in the ArrayList
						Thread concurrentFilter = new Thread(filter);
						threads.add(concurrentFilter);
						concurrentFilter.start();
					}	
					
					// if the job is a background job, add it to repl_jobs and then continue to next command immediately 
					if(isBackground) {
						// creates an ArrayList that stores the current thread running in the back ground and the user command for the thread
						ArrayList<Object> threadCMD = new ArrayList<Object>();
//						threadCMD.add(threads.get(threads.size()-1));
						threadCMD.add(threads);
						threadCMD.add(cmd + " &");
						// stores the thread info in the hashmap along with its job number
						repl_jobs.put(num, threadCMD);
						num++;
					}else {
						// busy wait until the last filter thread is complete if it is a foreground execution
						while(threads.get(threads.size()-1).isAlive()) {}
					}
				}catch (InvalidCommandException e) {
					System.out.print(e.getMessage());
				}
			}
		}
		
		System.out.print(Message.GOODBYE);
		consoleReader.close();

	}


}
