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
                "if(x==0):\n" +
                "    print('This won\'t not run)\n" +
                "    print('This won\'t not run)\n" +
                "    print('This won\'t not run)\n" +
                "    print('This won\'t not run)\n" +
                "else:\n" +
                "    print('This will run')\n";

//        while(true) {
//            Scanner myObj = new Scanner(System.in);
//            System.out.println("Welcome to the python interpreter, please enter the name of your file, followed by the .py extension.");
//            String filename = myObj.nextLine();
//            try {
//                in = Files.readString(Path.of(filename));
//                break;
//            } catch (IOException e) {
//                System.out.println("Could not read from file");
//            }
//        }

        String[] lines = in.split("\n");
        for (int i = 0; i < lines.length - 1;) {
            if (lines[i].matches("#.*")) {
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
        line = line.replaceAll("\\r", "");
        line = line.replaceAll("\\t", "    ");
        if (line.matches("[a-z0-9_]+ [-+*/^%]?= .*")){
            String[] lineSplit = line.split("=");
            line = lineSplit[0] + "=" + replaceVariables(lineSplit[1]);
            assignVariables(line);

        } else {
            line = replaceVariables(line);
        }

        if (line.matches("\\s*while.*")) {
            // Call while function
        }

        if (line.matches("\\s*for.*")) {
            // Call for function
        }

        if (line.matches("\\s*if.*")) {
            lineCount = readIf(lines, line, lineCount);
        }

        if (line.matches("\\s*(print\\(.*\\))"))
        {
            handlePrint(line);
        }

        return lineCount;
    }

    private static void handlePrint(String in) {
        String printContent = in.substring(in.indexOf("(") + 1, in.lastIndexOf(")"));
        if (printContent.contains("str(")) {
            printContent = printContent.replaceAll("str\\(", "");
            printContent = printContent.replaceAll("\\)", "");
        }

        if (printContent.contains("+")){
            printContent = printContent.replaceAll("\\s*\\+\\s*", "");
        }

        System.out.println(printContent.replaceAll("\"",""));
    }

    private static void assignVariables(String in) {

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
                newValue = tokens.get(2);
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
                if (tokens.size() > 3) {
                    String expression = in.substring(in.indexOf("=") + 2);
                    expression = expression.replaceAll("\\s", "");
                    newValue = String.valueOf(interpretMath(expression));
                }
                if (operation.equals("=")) {
                    vars.put(varName, newValue);
                } else {
                    System.out.println("Syntax Error: Variable " + varName + " not defined.");
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
        for (Map.Entry<String, String> entry: vars.entrySet()) {
            if (in.contains(entry.getKey())) {
                in = in.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            }
        }
        return in;
    }

    private static int readIf(String[] lines, String line, int lineCount) {
        String condition;
        int tabs = countTabs(line);

        if (line.matches("if\\(.*\\):")) {
            condition = line.replace("if(", "").replace("):", "");
        } else if (line.matches("if .*:")) {
            condition = line.replace("if ", "").replace(":", "");
        } else {
            System.out.println("Syntax Error: Invalid format for if statement");
            return lineCount;
        }

        boolean result = determineStatement(condition);
        if (result) {
            lineCount++;
            int lineTabs = tabs + 1;
            while (lineTabs >= tabs + 1) {
                lineTabs = countTabs(lines[lineCount]);
//                System.out.println("True line worked!");
                interpretLine(lines, lines[lineCount], lineCount);
                lineCount++;
            }
        } else {
            int nextLocation = lineCount + 1;
            while( countTabs(lines[nextLocation]) > tabs ) {
                nextLocation++;
            }
            System.out.println("Next Line to run: " + nextLocation);


            if((lines[nextLocation].matches("\\s*elif\\(.*\\):")||(lines[nextLocation].matches("\\s*elif .*:")))) {
            //check condition statement
                if (line.matches("elif\\(.*\\):")) {
                    condition = line.replace("elif(", "").replace("):", "");
                } else if (line.matches("elif .*:")) {
                    condition = line.replace("elif ", "").replace(":", "");
                } else {
                    System.out.println("Syntax Error: Invalid format for elif statement");
                    return lineCount;
                }

                if(determineStatement(condition))
                {
                    lineCount++;
                    int lineTabs = tabs + 1;
                    while (lineTabs >= tabs + 1) {
                        lineTabs = countTabs(lines[lineCount]);
//                        System.out.println("True line worked!");
                        interpretLine(lines, lines[lineCount], lineCount);
                        lineCount++;
                    }
                }

            }
            if (lines[nextLocation].matches("\\s*else\\(.*\\):")||(lines[nextLocation].matches("\\s*else .*:"))) { //if the next line is an elif) {
            //run code until tabs get >
            }

        }

        return lineCount;
    }

    private static double interpretMath(String in){
        String[] mathStrings = in.split("((?<=[-+*/%^])|(?=[-+*/%^]))(?![^\\(\\[]*[\\]\\)])((?<=[^\\d-])|(?=[^\\d-]))");
        //Assuming that there can be no nested parens (otherwise, you can't use a Java Regex for this task because recursive matching is not supported)

        double result = 0;
        String operation = "";
        for(String eq : mathStrings){
            if(eq.matches("\\(([^()]+)\\)")){
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
            else if(eq.matches("[-+]?[0-9]*\\.?[0-9]+")){
                double toNum = Double.parseDouble(eq);
                if(!(operation.equals("")))
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

    private static boolean determineStatement(String line)
    {
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

        return false;
    }
}
