package com.company;
import java.lang.Object;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files


public class Main {

    public static void main(String[] args) {

        String in = "print('Hello World')";
        if(in.contains("print")) {
            String[] parts = in.split("'", 3);
            System.out.println(parts[1]);
        }

        try {
            File myObj = new File("src/test.py");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);

                if(data.contains("if")){
                    System.out.println("found an if");
                    String condition  = data.split("[\\(\\)]")[1];
                    System.out.println("evaluating "+condition+" it's false.");
                    //evaluate(condition);
                    //fake condition for testing if above condition is true/false
                    Boolean con = false;
                    if(con){
                        while(true) {
                            data = myReader.nextLine();
                            //end if with blank line or an else:
                            if(data.equals("")) {
                                break;
                            }
                            else if(!data.contains("else")){
                                //execute(data);
                                System.out.println("\nnow executing " + data);
                            }
                            else{
                                break;
                            }
                        }
                    }
                    else{
                        while(true) {
                            data = myReader.nextLine();
                            if(data.contains("else")){
                                data = myReader.nextLine();
                               // execute(data);
                                System.out.println("\nnow executing " + data);
                            }
                            break;
                        }

                    }


                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
