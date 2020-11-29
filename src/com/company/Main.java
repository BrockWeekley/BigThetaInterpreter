package com.company;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        HashMap<String, Object> vars = new HashMap<>();

        String in = "name = 'haley'";
        String regex = "[a-z0-9_]+ [+*/^%\\-]?= .*";

        if (Pattern.matches(regex, in)){
            String[] tokens = in.split(" ");

            String varName = tokens[0];
            Object currentVal = vars.get(varName);

            String valueString = "";
            for(int i = 2; i < tokens.length; i++){
                valueString += tokens[i];
            }

            Object value = 0; //valueString.calculate();

            switch(tokens[1]){
                case "=":
                    if (currentVal != null){
                        vars.replace(varName, value);
                    } else {
                        vars.put(varName, value);
                    }
                    break;
                case "+=":
                    if (currentVal != null){
                        Object newVal = operation(currentVal, value, "+");
                        vars.replace(varName, newVal);
                    }
                    break;
                case "-=":
                    if (currentVal != null){
                        Object newVal = operation(currentVal, value, "-");
                        vars.replace(varName, newVal);
                    }
                    break;
                case "*=":
                    if (currentVal != null){
                        Object newVal = operation(currentVal, value, "*");
                        vars.replace(varName, newVal);
                    }
                    break;
                case "/=":
                    if (currentVal != null){
                        Object newVal = operation(currentVal, value, "/");
                        vars.replace(varName, newVal);
                    }
                    break;
                case "^=":
                    if (currentVal != null){
                        Object newVal = operation(currentVal, value, "^");
                        vars.replace(varName, newVal);
                    }
                    break;
                case "%=":
                    if (currentVal != null){
                        Object newVal = operation(currentVal, value, "%");
                        vars.replace(varName, newVal);
                    }
                    break;
            }
        }
    }
}
