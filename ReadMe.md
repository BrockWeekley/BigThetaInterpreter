#Big Theta Interpreter Project
###Members
1. Brock Weekley
2. Robert Truesdale
3. Haley Massa
4. Brad Schinker
5. Josh Ward



##Summary
At the start of the program, the interpreter will ask you for a file to interpret.
Once the user has provided a filename, we lookup that file and read it into a string.
We then parse this string line by line, interpreting each line with various keywords,
tabs, and syntax. Once we determine what that line is doing, we perform that operation
in Java.

We interpret each line in the following order:
##### First, we run each line through the interpretLine function.
   
   This will determine what the line is trying to do, do it, then return the index of the next line to run.
   1. First, we determine if there are any variables in the line. If there are, we replace them with the value
   we have held for that variable. Otherwise, if it is variable assignment, we store that variable to a HashMap
   with its associated value. EX: If x = 3, "y = 3+x", then the line becomes "y=3+3", and then y becomes 6.
   3. If it's a comment, we don't execute, we just go to the next line to execute. 
   4. If it's an if statement, we run the entire block of code. 
   For example, if the if statement is true, our function runs all of the lines within the if block. 
   Regardless, it will check for else ifs, and if none are true it will run the else block, 
   and return the location to run after it's complete (moving past any else or else if blocks that should not run.
   5. If it's a while block, we'll run any lines inside over and over as long as the condition is true. 
   We used a nested while. The inner while runs all the lines of the while block. The Outer checks the condition. 
   When we complete, we return the line after the while.
   6. If it's a for loop, we run a while loop the number of times found in the range function. The while loop
   runs the content of the for loop through the interpretLine function, also handling breaks.
   7. If it's a print statement, we print check for str() or int() functions, then print it with Java.
   Recall that any variables have been replaced, so we are able to handle string concatenation.

##### If a line has invalid syntax, we will break out and stop running lines,
informing the user of the syntax error.


## Running the Program

In order to run the program, we have precompiled it to run with a simple Java command.
Ensure you have the latest version of the Java JDK (JDK 15). Then, navigate to the src/ folder
within the project. For me, this path is ```C:\Users\Brock\Desktop\School\PoPL\interpreter\src```.
Then, run the command ```java com.company.Main```. This will run the program. The program will prompt
you for a file to run. Ensure you enter the package path before your file. Here's an example filepath:
 ```com/company/python_test_code.py```.
 You could also run the program in an IDE of your choice, but the filepath will likely just be
 ```python_test_code.py``` in that case.
 
 The program will then read each line of the file and perform the program in Java. It will error out at
 the end of the program with the syntax error uncommented and print an error message for the bonus points.