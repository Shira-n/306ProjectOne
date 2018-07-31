package model;

public class Notification {
    public static void message(String errorMessage){
        //Better to change this to a String cuz later we'll need to pass this to GUI as a popup
        StringBuffer s = new StringBuffer(errorMessage);
        s.append("\njava -jar scheduler.jar INPUT.dot P [OPTION]\n");
        s.append("INPUT.dot\ta task graph with integer weights in dot format\n");
        s.append("\n");
        s.append("Optional:\n");
        s.append("-p N\tuse N cores for execution in parallel (default is sequential\n");
        s.append("-v\tvisualise the search\n");
        s.append("-o OUTPUT\toutput file is named OUTPUT ( default is INPUT-output.dot");
        System.out.println(s);
    }

    public static void invalidInput(){
        message("Error: Invalid input");
    }
}
