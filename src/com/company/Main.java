package com.company;

import java.util.Arrays;
import java.lang.Math;

public class Main {

    public static void main(String[] args) {

        String in = "print('Hello World')";
        if(in.contains("print")) {
            String[] parts = in.split("'", 3);
            System.out.println(parts[1]);
        }
        in = "2 > 3";

        if(in.matches("\\d*\\.*\\d*<*>*=*\\d*\\.*\\d*"))
        {
            System.out.println(interpretLine(in,true));
        }

        in = "-(2 * 3) * 3";

        if (in.contains("+") || in.contains("-") || in.contains("*") || in.contains("/") || in.contains("%") || in.contains("^")){
            //String[] mathStrings = (in.replaceAll("\\s","")).split("((?<=[-+*/%^])|(?=[-+*/%^]))(?![^\\(\\[]*[\\]\\)])((?<=[^\\d-])|(?=[^\\d-]))");
            //System.out.print(Arrays.toString(mathStrings));
            System.out.println(interpretMath(in.replaceAll("\\s","")));
        }

    }
    public static boolean interpretLine(String line, boolean f)
    {
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
                    result = performOperaton(operation, result, toNum);
                else
                    result = toNum;
            }
            else{
                operation = eq;
            }
        }
        return result;
    }

    public static double performOperaton(String operation, double firstNum, double secondNum){
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
}
