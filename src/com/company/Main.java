package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    private static HashMap<String, String> vars = new HashMap<>();
    private static boolean broken = false;

    public static void main(String[] args) {
        String in;
        while(true) {
//            Scanner myObj = new Scanner(System.in);
            System.out.println("Welcome to the python interpreter, please enter the name of your file, followed by the .py extension.");
//            String filename = myObj.nextLine();
            String filename = "python_test_code.py";
            try {
                in = Files.readString(Path.of(filename));
                break;
            } catch (IOException e) {
                System.out.println("Could not read from file");
            }
        }

        in = in.replaceAll("\\r", "");
        String[] lines = in.split("\n");
        int i = 0;
        while(i < lines.length) {
            if (lines[i].matches("(\\s*#.*)|(^\\s*$)")) {
                i++;
                continue;
            }
            int j = interpretLine(lines, lines[i], i);
            if (i == j) {
                i++;
            } else if (j != -1) {
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

        if (broken) {
            return lineCount++;
        }

        if (line.matches("\\s*break")){
            broken = true;
        }

        if (line.matches("\\s*while.*")) {
            lineCount = whileLoop(lines, line, lineCount);
        }

        if (line.matches("\\s*for.*")) {
            lineCount = forLoop(lines, line, lineCount);
        }

        if (line.matches("\\s*if.*")) {
            lineCount = readIf(lines, line, lineCount);
        }

        if (line.matches("\\s*(print\\(.*\\))")) {
            handlePrint(line);
        }

        return lineCount;
    }


    private static int whileLoop(String[] lines, String line, int whileLine)
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

        int temp = whileLine;
        int whileTabs = countTabs(lines[temp]);
        boolean flag = determineStatement(line);
        while(flag) {
            temp = whileLine + 1;
            while (countTabs(lines[temp]) > whileTabs && !lines[temp].equals("")) {
                temp = interpretLine(lines, lines[temp], temp);
                temp++;
            }

            String whileStmt = lines[whileLine].replaceAll("while ", "").replace(":", "");
            flag = determineStatement(replaceVariables(whileStmt));
        }

        return temp;

    }

    private static int forLoop(String[] lines, String line, int forLine) {
        if (line.matches("\\s*for\\(.*\\):")){ //if we have a valid for loop sent to this function
            line = line.replace("for(","");
            line = line.substring(0, line.lastIndexOf(")")); // make line just the conditional statement

        } else if (line.matches("\\s*for.*:")) {
            line = line.replace("for ","");
            line = line.substring(0, line.lastIndexOf(":"));
        }
        else{
            System.out.println("Syntax Error: Invalid format for for statement");
        }

        if (line.contains("int(")) {
            Integer toInt = (int) interpretMath(line.substring(line.indexOf("int(") + 4, line.indexOf(")")));
            line = line.replaceAll("int\\(.*?\\)", toInt.toString());
            line = line.replaceAll("'", "");
            line = line.replaceAll("\"", "");
        }

        String forVariable = line.substring(0, line.indexOf("in") - 1);

        line = line.substring(line.indexOf("in") + 2, line.lastIndexOf(")"));
        line = line.replace("range(", "");
        double interpretLower = interpretMath(line.substring(1, line.indexOf(",")));
        double interpretUpper = interpretMath(line.substring(line.indexOf(",") + 2));
        int lower = (int) Math.floor(interpretLower);
        int upper = (int) Math.floor(interpretUpper);

        if (vars.get(forVariable) == null) {
            String assignmentStatement = forVariable + " = " + lower;
            assignVariables(assignmentStatement);
        } else {
            lower = (int) Math.floor(Double.parseDouble(vars.get(forVariable)));;
        }


        int temp = forLine;
        int forTabs = countTabs(lines[temp]);
        for (int i = lower; i < upper; i++) {
            temp = forLine + 1;
            while (countTabs(lines[temp]) > forTabs && !lines[temp].equals("")) {
                temp = interpretLine(lines, lines[temp], temp);
                temp++;
            }

            String nextIteration = forVariable + " += 1";
            assignVariables(nextIteration);

            if (broken) {
                broken = false;
                return -1;
            }
        }

        return temp;
    }

    private static void handlePrint(String in) {
        String printContent = in.substring(in.indexOf("(") + 1, in.lastIndexOf(")"));
        if (printContent.contains("str(")) {
            printContent = printContent.replaceAll("str\\(", "");
            printContent = printContent.replaceAll("\\)", "");
            printContent = replaceVariables(printContent);
        }

        if (printContent.contains("int(")) {
            printContent = printContent.replaceAll("int\\(", "");
            printContent = printContent.replaceAll("\\..*?\\)", "");

            printContent = printContent.replaceAll("'", "");
            printContent = printContent.replaceAll("\"", "");

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
        //System.out.println(varName + ": " + vars.get(varName));
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
                    if (in.contains("in")) {
                        if (in.substring(0, in.indexOf("in")).contains(entry.getKey())) {
                            continue;
                        }
                    }
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

                    if (line.matches("\\s*elif\\(.*\\):")) {
                        condition = line.replace("elif(", "").replace("):", "");
                    } else if (line.matches("\\s*elif .*:")) {
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
                        nextLocation = interpretLine(lines, lines[nextLocation], nextLocation);
                        nextLocation++;
                    }
                    return nextLocation;
                }
            }
            lineCount = nextLocation;
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
            lineCount = interpretLine(lines, lines[lineCount], lineCount);
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

        if (line.contains("and")) {
            int trueCount = 0;
            String[] statementArray = line.split(" and ");
            for (String statement : statementArray) {
                if (determineStatement(statement.replaceAll("[\\(\\)]", ""))) trueCount++;
            }

            if (trueCount == statementArray.length) return true;
            else return false;
        }
        if (line.contains("or")) {
            String[] statementArray = line.split(" or ");
            int trueCount = 0;
            for (String statement : statementArray) {
                if (determineStatement(statement.replaceAll("[\\(\\)]", ""))) return true;
            }

            return false;
        }

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
        else if(line.matches("True"))
            return true;
        else
            return false;
    }
}
