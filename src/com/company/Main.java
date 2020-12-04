package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static List<String> keywords = new ArrayList<>(){{
        add("while");
        add("for");
        add("print");
        add("if");
    }};

    public static HashMap<String, String> vars = new HashMap<>();

    public static void main(String[] args) {

        String in = "";
        try {
            in = Files.readString(Path.of("practice.py"));
        } catch (IOException e) {
            System.out.println("Could not read from file");
        }

        String[] lines = in.split("\n");
        int i = 0;
        int j = i;
        for (String line: lines) {

            //TODO: call get variable on the line, return the line with the value in it

            if (line.contains(" while ")) {
                // Call while function
            }

            if (line.contains(" for ")) {
                // Call for function
            }

            if (line.contains(" if ")) {
                String data = line;
                String condition = data.split("[\\(\\)]")[1];
                boolean result = interpretLine(condition);
                if (result) {
                    while (true) {
                        data = lines[j + 1];
                        j = i + 1;
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
                        j = i + 1;
                        if (data.contains("else:")) {
                            data = lines[j + 1];
                            j = i + 1;
                            // execute(data);
                            System.out.println("\nnow executing " + data);
                        } else if (data.equals("")) {
                            break;
                        }
                    }
                }
            }
            if (line.contains(" print ")) {
                // Call print function
            }

            i++;
        }

    }

    private static void getVariables(String in) {
        in = in.replaceAll("\\r", "");
        String regex = "[a-z0-9_]+ [-+*/^%]?= .*";

        if (Pattern.matches(regex, in)){
            String[] tokens = in.split(" ");

            String varName = tokens[0];
            Double currentVal;
            try {
                currentVal = Double.parseDouble(vars.get(varName));
            } catch(NullPointerException e) {
                currentVal = null;
            }

            StringBuilder sb = new StringBuilder();
            for(int i = 2; i < tokens.length; i++) {
                sb.append(tokens[i]);
            }

            Double value = interpretMath(sb.toString());

            switch(tokens[1]){
                case "=":
                    if (currentVal != null){
                        vars.replace(varName, value.toString());
                    } else {
                        vars.put(varName, value.toString());
                    }
                    break;
                case "+=":
                    if (currentVal != null){
                        Object newVal = performOperation("+", currentVal, value);
                        vars.replace(varName, newVal.toString());
                    }
                    break;
                case "-=":
                    if (currentVal != null){
                        Object newVal = performOperation("-", currentVal, value);
                        vars.replace(varName, newVal.toString());
                    }
                    break;
                case "*=":
                    if (currentVal != null){
                        Object newVal = performOperation("*", currentVal, value);
                        vars.replace(varName, newVal.toString());
                    }
                    break;
                case "/=":
                    if (currentVal != null){
                        Object newVal = performOperation("/", currentVal, value);
                        vars.replace(varName, newVal.toString());
                    }
                    break;
                case "^=":
                    if (currentVal != null){
                        Object newVal = performOperation("^", currentVal, value);
                        vars.replace(varName, newVal.toString());
                    }
                    break;
                case "%=":
                    if (currentVal != null){
                        Object newVal = performOperation("%", currentVal, value);
                        vars.replace(varName, newVal.toString());
                    }
                    break;
            }
        }
    }

    private static double interpretMath(String in){
        String[] mathStrings = in.split("((?<=[-+*/%^])|(?=[-+*/%^]))(?![^\\(\\[]*[\\]\\)])");
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
            else if(eq.matches("\\d*\\.*\\d")){
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
        double result = 0;

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

    public static boolean interpretLine(String line)
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
