package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
            if (line.contains(" while ")) {
                // Call while function
            }
            if (line.contains(" for ")) {
                // Call for function
            }
            if (line.contains(" if ")) {
                String data = line;
                System.out.println("found an if");
                String condition = data.split("[\\(\\)]")[1];
                System.out.println("evaluating " + condition + " it's false.");
                //evaluate(condition);
                //fake condition for testing if above condition is true/false
                Boolean con = false;
                if (con) {
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
            getVariables(line);
            i++;
        }

    }

    private static void getVariables(String in) {
        HashMap<String, String> variables = new HashMap<>();
        Pattern equals = Pattern.compile("=+");
        Matcher matcher = equals.matcher(in);
        while (matcher.find()) {
//            System.out.print("Start index: " + matcher.start());
//            System.out.print(" End index: " + matcher.end());
//            System.out.println(" Found: " + matcher.group());
            if (matcher.end() - 1 == matcher.start()) {
                int position = matcher.start() - 2;
                int rightPosition = matcher.start() + 2;
                char iterator = in.charAt(position);
                char rightIterator = in.charAt(rightPosition);
                StringBuilder expression = new StringBuilder();
                StringBuilder variable = new StringBuilder();
                while (iterator != ' ' && iterator != '\n') {
                    variable.insert(0, iterator);
                    position -= 1;
                    if (position >= 0) {
                        iterator = in.charAt(position);
                    } else {
                        break;
                    }
                }

                while (rightIterator != '\r') {
                    expression.append(rightIterator);
                    rightPosition += 1;
                    rightIterator = in.charAt(rightPosition);
                }

//                System.out.println(expression);

                if (!variables.containsKey(variable.toString()) && !keywords.contains(variable.toString()) && !variable.toString().equals("")) {
                    variables.put(variable.toString(), "");
                } else if (variables.containsKey(variable.toString())) {
                    // This is where we perform operations on the variable
                }
            }
        }
        for (String variable : variables.keySet()) {
            System.out.println(variable);
        }
    }
}
