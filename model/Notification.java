package model;

public class Notification {
    public static void message(String errorMessage){
        //Better to change this to a String cuz later we'll need to pass this to GUI as a popup
        System.out.println(errorMessage);
        System.out.println("java -jar scheduler.jar INPUT.dot P [OPTION]\n");
        System.out.println("INPUT.dot\ta task graph with integer weights in dot format\n");
        System.out.println("\n");
        System.out.println("Optional:\n");
        System.out.println("-p N\tuse N cores for execution in parallel (default is sequential\n");
        System.out.println("-v\tvisualise the search\n");
        System.out.println("-o OUTPUT\toutput file is named OUTPUt ( default is INPUT-output.dot");
    }
    public static void invalidInput(){
        message("Error: Invalid input");
    }
}
