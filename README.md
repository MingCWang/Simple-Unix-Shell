# Simple-Unix-Shell
This project studies how the shell works, how pipes, foreground and background processing all go together by creating a simplified unix shell. I developed a deeper understanding of concurrency through the process of debugging race conditions and creating critical sections. 

## Implemented Commands
The user has the option to make a process run in the "background" by adding the ampersand (&) as the last character on the line.

```pwd```\t\t\t\tPrint current working directory <br />
```cd```        Change directory <br />
```cat```       Read file content and redirect to STDOUT or pipe <br />
```head/tail``` Returns up to the first/last 10 lines from piped input <br />
```grep```      Returns all lines from piped input that contain the taraget string <br />
```wc```        Counts the number of lines, words, and characters in the piped input <br />
```ls```        Lists the files in the current working directory <br />
```uniq```      Removes any line from the piped input that is the same as the previous line <br />
```cc <r>```    Applies a Caesar cipher to the piped input with the provided rotation <r> <br />
```|```         This allows piping commands togather <br />
```>```         Redirects the output to a text file <br />
```repl_jobs``` Checks which of the background processes are still alive and prints a list of the alive processes <br />
```kill```      Kill a specified command by providing the number that “repl_jobs” assigned to it <br />


## Example Output
<img width="805" alt="Screen Shot 2023-04-15 at 2 10 01 PM" src="https://user-images.githubusercontent.com/73949957/232246228-b4178fed-af82-4f0f-836b-055b9afc805f.png">
