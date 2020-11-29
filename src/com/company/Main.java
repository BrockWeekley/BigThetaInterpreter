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

        getVariables(in);

//        String in = "print('Hello World')";
//        if(in.contains("print")) {
//            String[] parts = in.split("'", 3);
//            System.out.println(parts[1]);
//        }

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

                while (rightIterator != '\n') {
                    expression.append(rightIterator);
                    rightPosition += 1;
                    rightIterator = in.charAt(rightPosition);
                }

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
