#Big Theta Interpreter Project
###Members
1. Brock Weekley
2. Robert Truesdale
3. Haley
4. Brad S
5. Josh Ward



##Summary
The main function will ask for a file name. Then, we load an array of string. 
Each line of the file will be put into its own string. The location of a line stored in the array is as follows: 
lines[(line number in file - 1)]




We interpret each line in the following order.
#####We run each line through the interpretLine function.
   
   We determine what the line is trying to do, do it, then return the next line to run.
   1. First, if there is any variable in the line, we replace them. EX: "x=3 \n y=x+2" becomes "x=3 \n y=3+2".
   2. If it's a variable assignment, we compute the variables value, and store it inside a hashmap. 
   3. If it's a comment, we don't execute, we just go to the next line to execute. 
   4. If it's an if statement, we run the entire block of code. For example, if the if statement is true, our function runs all of the lines within the if block. Regardless, it will check for elif's, and if none are true it will run the else block, and return the location to run after it's complete.
   5. If it's a while block, we'll run any lines inside over and over as long as the condition is true. We used a nested while. The inner while runs all the lines of the while block. The Outer checks the condition. 
   6. If it's a print statement, we print it. Recall that any variables have been replaced, so we can print variables if their in a sting.

##### If a line has invalid syntax, we will break out and stop running lines, informing the user of the syntax error.