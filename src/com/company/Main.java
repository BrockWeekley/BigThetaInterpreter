package com.company;

public class Main {

    public static void main(String[] args) {

        String in = "print('Hello World')";
        if(in.contains("print")) {
            String[] parts = in.split("'", 3);
            System.out.println(parts[1]);
        }

    }
}
