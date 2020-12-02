package com.company;

public class Main {

    public static void main(String[] args) {

        String in = "print('Hello World')";
        if(in.contains("print")) {
            String[] parts = in.split("'", 3);
            System.out.println(parts[1]);
        }
        in = "2>3";

        if(in.matches("\\d*\\.*\\d*<*>*=*\\d*\\.*\\d*"))
        {
            System.out.println(interpretLine(in,true));
        }

    }
    public static boolean interpretLine(String line, boolean f)
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
