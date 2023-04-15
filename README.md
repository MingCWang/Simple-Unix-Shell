# Simple-Unix-Shell
This project studies how the shell works, how pipes, foreground and background processing all go together by creating a simplified unix shell. I developed a deeper understanding of concurrency through the process of debugging race conditions and creating critical sections. 

## Implemented Commands
The user has the option to make a process run in the "background" by adding the ampersand (&) as the last character on the line.

- ```pwd```       Print current working directory <br />
- ```cd```        Change directory <br />
- ```cat```       Read file content and redirect to STDOUT or pipe <br />
- ```head/tail``` Returns up to the first/last 10 lines from piped input <br />
- ```grep```      Returns all lines from piped input that contain the taraget string <br />
- ```wc```        Counts the number of lines, words, and characters in the piped input <br />
- ```ls```        Lists the files in the current working directory <br />
- ```uniq```      Removes any line from the piped input that is the same as the previous line <br />
- ```cc <r>```    Applies a Caesar cipher to the piped input with the provided rotation <r> <br />
- ```|```         This allows piping commands togather <br />
- ```>```         Redirects the output to a text file <br />
- ```repl_jobs``` Checks which of the background processes are still alive and prints a list of the alive processes <br />
- ```kill```      Kill a specified command by providing the number that “repl_jobs” assigned to it <br />


## Example Output
### Basic shell commands
  <img width="771" alt="Screen Shot 2023-04-15 at 5 36 48 PM" src="https://user-images.githubusercontent.com/73949957/232254128-e2516b60-e8c3-4b33-8a39-9c62f5b0b1cf.png">
  
### Background process demonstration
test.txt is a file that contains over 100000000 lines. This output shows how the process is being put in the background. 
  <img width="759" alt="Screen Shot 2023-04-15 at 5 40 10 PM" src="https://user-images.githubusercontent.com/73949957/232254235-56b948c6-9a09-4cb1-81f4-9861068a6efc.png">


