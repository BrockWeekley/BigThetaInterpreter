package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
//    private static List<String> keywords = new ArrayList<>(){{
//        add("while");
//        add("for");
//        add("print");
//        add("if");
//    }};

    private static HashMap<String, String> vars = new HashMap<>();

    public static void main(String[] args) {
        String in = "y = 6 \n" +
                "x = (y + y) % 2\n" +
                "if(x==1):\n" +
                "    print('Inside the if')\n" +
                "    print('Inside the if')\n" +
                "elif(x==2):\n" +
                "    print('Inside the elif')\n" +
                "    print('Inside the elif')\n" +
                "else:\n" +
                "    print('Inside the else')\n" +
                "\n" +
                "y = 4\n" +
                "print(y)";
        while(true) {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Welcome to the python interpreter, please enter the name of your file, followed by the .py extension.");
            String filename = myObj.nextLine();
            try {
                in = Files.readString(Path.of(filename));
                break;
            } catch (IOException e) {
                System.out.println("Could not read from file");
            }
        }

        in = in.replaceAll("\\r", "");
        String[] lines = in.split("\n");
//        for (int i = 0; i < lines.length;) {
        int i = 0;
        while(i < lines.length) {
            if (lines[i].matches("\\s*#.*")) {
                i++;
                continue;
            }
            int j = interpretLine(lines, lines[i], i);
            if (i == j) {
                i++;
            } else {
                i = j;
            }
        }

    }

    private static int interpretLine(String[] lines, String line, int lineCount) {
        line = line.replaceAll("\\t", "    ");
        if (line.matches("\\s*[a-zA-Z0-9_]+ [-+*/^%]?= .*")){
            String[] lineSplit = line.split("=");
            line = lineSplit[0] + "=" + replaceVariables(lineSplit[1]);
            assignVariables(line);
        } else {
            line = replaceVariables(line);
        }

        if (line.matches("\\s*while.*")) {
            whileLoop(lines, line, lineCount);
        }

        if (line.matches("\\s*for.*")) {
            // Call for function
        }

        if (line.matches("\\s*if.*")) {
            lineCount = readIf(lines, line, lineCount);
        }

        if (line.matches("\\s*(print\\(.*\\))")) {
            handlePrint(line);
        }

        return lineCount;
    }


    private static void whileLoop(String[] lines, String line, int whileLine)
    {
        if (line.matches("\\s*while\\(.*\\):")){ //if we have a valid for loop sent to this function
            line = line.replace("while(","");
            line = line.substring(0, line.lastIndexOf(")")); // make line just the conditional statement

        } else if (line.matches("\\s*while.*:")) {
            line = line.replace("while ","");
            line = line.substring(0, line.lastIndexOf(":"));
        }
        else{
            System.out.println("Syntax Error: Invalid format for while statement");
        }

        int temp =  whileLine;
        int whileTabs = countTabs(lines[temp]);
        boolean flag = determineStatement(line);
        while(flag) {
            temp = whileLine + 1;
            while (countTabs(lines[temp]) > whileTabs && !lines[temp].equals("")) {
                temp = interpretLine(lines, lines[temp], temp);
                temp++;
            }

            String whileStmt = lines[whileLine].replaceAll("while ", "").replace(":", "");
            System.out.println(replaceVariables(whileStmt));
            flag = determineStatement(replaceVariables(whileStmt));
        }


    }


    private static void handlePrint(String in) {
        String printContent = in.substring(in.indexOf("(") + 1, in.lastIndexOf(")"));
        if (printContent.contains("str(")) {
            printContent = printContent.replaceAll("str\\(", "");
            printContent = printContent.replaceAll("\\)", "");
            printContent = replaceVariables(printContent);
        }

        if (printContent.contains("+")){
            printContent = printContent.replaceAll("\\s*\\+\\s*", "");
        }

        System.out.println(printContent.replaceAll("\"",""));
    }

    private static void assignVariables(String in) {
        while (in.matches(" .*")) {
            in = in.substring(1);
        }
        List<String> tokens = new ArrayList<>(Arrays.asList(in.split(" ")));

        String varName = tokens.get(0);
        String previousValue;
        try {
            previousValue = vars.get(varName);
        } catch(NullPointerException e) {
            previousValue = null;
        }

        String newValue;
        ArrayList<String> expressionTokens = new ArrayList<>();
        boolean isString = false;
        if (tokens.get(2).contains("\"") && tokens.get(tokens.size() - 1).contains("\"")) {
            for(int i = 2; i < tokens.size(); i++){
                expressionTokens.add(tokens.get(i));
            }
            newValue = String.join(" ", expressionTokens);
            isString = true;
        } else {
            newValue = String.valueOf(interpretMath(tokens.get(2)));
        }

        String operation = tokens.get(1);
        if (previousValue != null){
            if (!operation.equals("=")) {
                if (!isString) {
                    newValue = String.valueOf(performOperation(operation.substring(0, 1), Double.parseDouble(previousValue), Double.parseDouble(newValue)));
                }
            }
            vars.replace(varName, newValue);
        } else {
            if (tokens.size() > 3 && !isString) {
                String expression = in.substring(in.indexOf("=") + 2);
                expression = expression.replaceAll("\\s", "");
                newValue = String.valueOf(interpretMath(expression));
            }
            if (operation.equals("=")) {
                vars.put(varName, newValue);
            } else {
                System.out.println("Syntax Error: Variable " + varName + " not defined.");
//                System.exit(0);
            }
        }
        System.out.println(varName + ": " + vars.get(varName));
    }

    private static int countTabs(String line) {
        int spaceCount = 0;
        int tabCount = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                spaceCount++;
            } else {
                break;
            }
            if (spaceCount == 4) {
                tabCount += 1;
                spaceCount = 0;
            }
        }
        return tabCount;
    }

    private static String replaceVariables(String in) {
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            if (in.contains(entry.getKey())) {
                if (!in.matches("\\s*print\\(\'.*"+ entry.getKey() +".*\'\\)") &&
                        !in.matches("\\s*print\\(\".*"+ entry.getKey() +".*\"\\)")) {
                    in = in.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
                }
            }
        }
        return in;
    }

    private static int readIf(String[] lines, String line, int lineCount) {
        String condition;
        int tabs = countTabs(line);

        if (line.matches("\\s*if\\(.*\\):")) {
            condition = line.replace("if(", "").replace("):", "");
        } else if (line.matches("\\s*if .*:")) {
            condition = line.replace("if ", "").replace(":", "");
        } else {
            System.out.println("Syntax Error: Invalid format for if statement");
//            System.exit(0);
            return lineCount;
        }

        boolean result = determineStatement(condition);
        if (result) {
            lineCount = trueIf(lines, tabs, lineCount);
        } else {
            int nextLocation = lineCount + 1;
            while( countTabs(lines[nextLocation]) > tabs ) {
                nextLocation++;
                line = lines[nextLocation];
                if ((line.matches("\\s*elif\\(.*\\):") || (line.matches("\\s*elif .*:")))) {

                    if (line.matches("elif\\(.*\\):")) {
                        condition = line.replace("elif(", "").replace("):", "");
                    } else if (line.matches("elif .*:")) {
                        condition = line.replace("elif ", "").replace(":", "");
                    } else {
                        System.out.println("Syntax Error: Invalid format for elif statement");
//                        System.exit(0);
                        return nextLocation;
                    }
                    condition = replaceVariables(condition);
                    if (determineStatement(condition)) {
                        lineCount = trueIf(lines, tabs, nextLocation);
                        return lineCount;
                    } else {
                        nextLocation++;
                    }

                }
                if (lines[nextLocation].matches("\\s*else:")) {
                    nextLocation++;
                    int lineTabs = tabs + 1;
                    while (lineTabs >= tabs + 1) {
                        lineTabs = countTabs(lines[nextLocation]);
                        interpretLine(lines, lines[nextLocation], nextLocation);
                        nextLocation++;
                    }
                    return nextLocation;
                }
            }

        }

        return lineCount;
    }

    private static int trueIf(String[] lines, int tabs, int lineCount) {
        lineCount++;
        int lineTabs;
        while (true) {
            lineTabs = countTabs(lines[lineCount]);
            if (lineTabs < tabs + 1) {
                break;
            }
            interpretLine(lines, lines[lineCount], lineCount);
            lineCount++;
        }

        int skipCount = lineCount;
        lineTabs = tabs + 1;
        int i = 0;
        while (lineTabs >= tabs + 1 || (lines[skipCount].matches("\\s*elif\\(.*\\):") ||
                lines[skipCount].matches("\\s*elif .*:") || lines[skipCount].matches("\\s*else:"))) {
            skipCount++;
            try {
                lineTabs = countTabs(lines[skipCount]);
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
            if (i > 0) {
                lineCount = skipCount;
            }
            i++;
        }
        return lineCount;
    }

    private static double interpretMath(String in){
        String[] mathStrings = in.split("((?<=[-+*/%^])|(?=[-+*/%^]))(?![^\\(\\[]*[\\]\\)])((?<=[^\\d-])|(?=[^\\d-]))");
        //Assuming that there can be no nested parens (otherwise, you can't use a Java Regex for this task because recursive matching is not supported)

        double result = 0;
        String operation = "";
        for (String eq : mathStrings){
            if (eq.matches("\\(([^()]+)\\)")){
                double parResult = interpretMath(eq.replaceAll("[()]", ""));
                switch (operation){
                    case "+":
                        result += parResult;
                        break;
                    case "-":
                        result -= parResult;
                        break;
                    case "*":
                        result *= parResult;
                        break;
                    case "/":
                        result /= parResult;
                        break;
                    case "%":
                        result %= parResult;
                        break;
                    case "^":
                        result = Math.pow(result, parResult);
                        break;
                    default:
                        result = parResult;
                }
            }
            else if (eq.matches("[-+]?[0-9]*\\.?[0-9]+")){
                double toNum = Double.parseDouble(eq);
                if (!(operation.equals("")))
                    result = performOperation(operation, result, toNum);
                else
                    result = toNum;
            }
            else{
                operation = eq;
            }
        }
        return result;
    }

    private static double performOperation(String operation, double firstNum, double secondNum){
        double result;

        switch (operation){
            case "+":
                result = firstNum + secondNum;
                break;
            case "-":
                result = firstNum - secondNum;
                break;
            case "*":
                result = firstNum * secondNum;
                break;
            case "/":
                result = firstNum / secondNum;
                break;
            case "%":
                result = firstNum % secondNum;
                break;
            case "^":
                result = Math.pow(firstNum, secondNum);
                break;
            default:
                System.out.println("Something went wrong with " + operation);
                result = 0;
        }
        return result;
    }

    private static boolean determineStatement(String line) {
        if (line.contains("and") || line.contains("or")){
            String[] statementArray = line.split("(((?<= and)|(?= and))|((?<= or)|(?= or)))(?![^()]*\\))");

            int stateNum = 0;
            boolean wholeCond = false;
            for (String statement : statementArray){

                if (statement.matches("\\s*\\(([^()]+)\\)")){
                    statementArray[stateNum] = String.valueOf(determineStatement(statement.replaceAll("\\s*[()]", "")));
                }
                statement = statement.replaceAll("\\s","");
                if (statement.matches("and")){
                    try{
                        wholeCond = determineStatement(statementArray[stateNum - 1].replaceAll("\\s*[()]", ""))
                                && determineStatement(statementArray[stateNum + 1].replaceAll("\\s*[()]", ""));
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("Syntax Error: Invalid format for and condition statement");
                        System.exit(0);
                    }
                }
                else if (statement.matches("or")){
                    try{
                        wholeCond = determineStatement(statementArray[stateNum - 1].replaceAll("\\s*[()]", ""))
                                || determineStatement(statementArray[stateNum + 1].replaceAll("\\s*[()]", ""));
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("Syntax Error: Invalid format for or condition statement");
                        System.exit(0);
                    }
                }
                else{
                    stateNum++;
                    continue;
                }
                stateNum++;
            }
            if (wholeCond)
                return true;
            else
                return false;
        }
        else{
            line = line.replaceAll("\\s","");
            if(line.matches("\\d*\\.*\\d*<\\d*\\.*\\d*"))
            {
                double x = Double.parseDouble(line.split("<")[0]);
                double y = Double.parseDouble(line.split("<")[1]);
                return x < y;
            }
            else if(line.matches("\\d*\\.*\\d*<=\\d*\\.*\\d*"))
            {
                double x = Double.parseDouble(line.split("<=")[0]);
                double y = Double.parseDouble(line.split("<=")[1]);
                return x <= y;
            }
            else if(line.matches("\\d*\\.*\\d*>\\d*\\.*\\d*"))
            {
                double x = Double.parseDouble(line.split(">")[0]);
                double y = Double.parseDouble(line.split(">")[1]);
                return x > y;
            }
            else if(line.matches("\\d*\\.*\\d*>=\\d*\\.*\\d*"))
            {
                double x = Double.parseDouble(line.split(">=")[0]);
                double y = Double.parseDouble(line.split(">=")[1]);
                return x >= y;
            }
            else if(line.matches("\\d*\\.*\\d*==\\d*\\.*\\d*"))
            {
                double x = Double.parseDouble(line.split("==")[0]);
                double y = Double.parseDouble(line.split("==")[1]);
                return x == y;
            }
            else if(line.matches("\\d*\\.*\\d*!=\\d*\\.*\\d*"))
            {
                double x = Double.parseDouble(line.split("!=")[0]);
                double y = Double.parseDouble(line.split("!=")[1]);
                return x != y;
            }
            else if(line.matches("true"))
                return true;
            else
                return false;
        }
    }
}
