# Simple-Unix-Shell
This project studies how the shell works, how pipes, foreground and background processing all go together by creating a simplified unix shell. I developed a deeper understanding of concurrency through the process of debugging race conditions and creating critical sections. 

## Implemented Commands
The user has the option to make a process run in the "background" by adding the ampersand (&) as the last character on the line.
```bash
$ pwd - **Print current working directory**
$ cd - Change directory
$ cat - Read file content and redirect to STDOUT or pipe
$ head/tail - Returns up to the first/last 10 lines from piped input.
$ grep - Returns all lines from piped input that contain the taraget string
$ wc - Counts the number of lines, words, and characters in the piped input.
$ ls - Lists the files in the current working directory
$ uniq - Removes any line from the piped input that is the same as the previous line.
$ cc <r> - Applies a Caesar cipher to the piped input with the provided rotation <r>.
$ | - This allows piping commands togather
$ \> - redirects the output to a text file
$ repl_jobs - Checks which of the background processes are still alive and prints a list of the alive processes.
$ kill - kill a specified command by providing the number that “repl_jobs” assigned to it. 
```

## Example Output
<img width="805" alt="Screen Shot 2023-04-15 at 2 10 01 PM" src="https://user-images.githubusercontent.com/73949957/232246228-b4178fed-af82-4f0f-836b-055b9afc805f.png">
