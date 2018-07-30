package model;

public class Notification {
    public static void message(){
        System.out.println("java -jar scheduler.jar INPUT.dot P [OPTION]\n");
        System.out.println("INPUT.dot\ta task graph with integer weights in dot format\n");
        System.out.println("\n");
        System.out.println("Optional:\n");
        System.out.println("-p N\tuse N cores for execution in parallel (default is sequential\n");
        System.out.println("-v\tvisualise the search");
        System.out.println("-o OUTPUT\toutput file is named OUTPUt ( default is INPUT-output.dot");
    }
}
