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
        String in;
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


        String[] lines = in.split("\n");
        int i = 0;
        for (String line: lines) {

            if (line.contains("#")) {
                continue;
            }

            line = replaceVariables(line);

            if (line.matches("\\s*(while ).*")) {
                // Call while function
            }

            if (line.matches("\\s*(for ).*")) {
                // Call for function
            }

            if (line.matches("\\s*(if ).*")) {
                readIf(line, lines, i);
            }
            if (line.matches("\\s*(print\\(.*\\))"))
            {
                handlePrint(line);
            }
            i++;
        }

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
                newValue = String.valueOf(interpretMath(tokens.get(2)));
            }

            String operation = tokens.get(1);
            if (previousValue != null){
                if (!operation.equals("=")) {
                    if (!isString) {
                        newValue = String.valueOf(performOperation(operation.substring(0, 0), Double.parseDouble(previousValue), Double.parseDouble(newValue)));
                    }
                }
                vars.replace(varName, newValue);
            } else {
                if (operation.equals("=")) {
                    vars.put(varName, newValue);
                } else {
                    System.out.println("Syntax Error: Variable " + varName + " not defined.");
                }
            }
    }

    private static String replaceVariables(String in) {
        in = in.replaceAll("\\r", "");
        String regex = "[a-z0-9_]+ [-+*/^%]?= .*";

        if (Pattern.matches(regex, in)){
            assignVariables(in);
        } else {
            for (Map.Entry<String, String> entry: vars.entrySet()) {
                if (in.contains(entry.getKey())) {
                    in = in.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
                }
            }
        }
        return in;
    }

    private static void readIf(String in, String[] lines, int currentLineIndex) {
        String data = in;
        int j = currentLineIndex;
        String condition = data.replace("if ", "").replace(":", "");
//        String condition = data.split("[\\(\\)]")[1];
        boolean result = determineStatement(condition);
        if (result) {
            while (true) {
                data = lines[j + 1];
                j = currentLineIndex + 1;
                //end if with blank line or an else:
                if (data.equals("")) {
                    break;
                } else if (!data.contains("else")) {
                    //execute(data);
                    System.out.println("\nnow executing " + data);
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                data = lines[j + 1];
                j = currentLineIndex + 1;
                if (data.contains("else:")) {
                    data = lines[j + 1];
                    j = currentLineIndex + 1;
                    // execute(data);
                    System.out.println("\nnow executing " + data);
                } else if (data.equals("")) {
                    break;
                }
            }
        }
    }

    public static double interpretMath(String in){
        String[] mathStrings = in.split("((?<=[-+*/%^])|(?=[-+*/%^]))(?![^\\(\\[]*[\\]\\)])((?<=[^\\d-])|(?=[^\\d-]))");
        //Assuming that there can be no nested parens (otherwise, you can't use a Java Regex for this task because recursive matching is not supported)

        System.out.print(Arrays.toString(mathStrings));

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
            return x<y;
        }
        else if(line.matches("\\d*\\.*\\d*<=\\d*\\.*\\d*"))
        {
            double x = Double.parseDouble(line.split("<=")[0]);
            double y = Double.parseDouble(line.split("<=")[1]);
            return x<=y;
        }
        else if(line.matches("\\d*\\.*\\d*>\\d*\\.*\\d*"))
        {
            double x = Double.parseDouble(line.split(">")[0]);
            double y = Double.parseDouble(line.split(">")[1]);
            return x>y;
        }
        else if(line.matches("\\d*\\.*\\d*>=\\d*\\.*\\d*"))
        {
            double x = Double.parseDouble(line.split(">=")[0]);
            double y = Double.parseDouble(line.split(">=")[1]);
            return x>=y;
        }
        else if(line.matches("\\d*\\.*\\d*==\\d*\\.*\\d*"))
        {
            double x = Double.parseDouble(line.split("==")[0]);
            double y = Double.parseDouble(line.split("==")[1]);
            return x<=y;
        }
        else if(line.matches("\\d*\\.*\\d*!=\\d*\\.*\\d*"))
        {
            double x = Double.parseDouble(line.split("!=")[0]);
            double y = Double.parseDouble(line.split("!=")[1]);
            return x!=y;
        }

        return false;
    }
}
