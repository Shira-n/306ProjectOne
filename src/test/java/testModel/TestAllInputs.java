package testModel;

import application.Main;
import org.junit.*;

import java.util.*;

public class TestAllInputs {

    private List<String> _fileNames = new ArrayList<>();

    @Before
    public void initialise() {
        _fileNames.add("input");
        _fileNames.add("input2");
        _fileNames.add("input3");
        _fileNames.add("input5");
        _fileNames.add("input_14Nodes_203words7dvi_0edgecost");
        _fileNames.add("test");
        _fileNames.add("test2");
        _fileNames.add("test3");

    }

    @Test
    public void testMain(){
        for(String fileName : _fileNames) {
            System.out.println("testing "+fileName);
            String[] args = {fileName + ".dot", "2"};
            Main.main(args);
            System.out.println("");
        }

    }

    @Test
    public void testMain1(){
        List<String> fileNames = new ArrayList<>();

        fileNames.add("Nodes_7_OutTree");
        fileNames.add("Nodes_8_Random");
        fileNames.add("Nodes_9_SeriesParallel");
        fileNames.add("Nodes_10_Random");
        fileNames.add("Nodes_11_OutTree");
        for(String fileName : fileNames) {
            System.out.println("testing "+fileName);
            String[] args = {fileName + ".dot", "2"};
            Main.main(args);
            System.out.println(" ");
        }

    }

    @Test
    public void testParallel(){

        for(String fileName : _fileNames) {
            System.out.println("testing "+fileName);
            String[] args = {fileName + ".dot", "2", "-p", "2"};
            Main.main(args);
            System.out.println("");
        }

    }

    @Test
    public void testParallel2(){
        List<String> fileNames = new ArrayList<>();

        fileNames.add("Nodes_7_OutTree");
        fileNames.add("Nodes_8_Random");
        fileNames.add("Nodes_9_SeriesParallel");
        fileNames.add("Nodes_10_Random");
        fileNames.add("Nodes_11_OutTree");
        for (String fileName : fileNames) {
            System.out.println("testing " + fileName);
            String[] args = {fileName + ".dot", "2", "-p", "2"};
            Main.main(args);
            System.out.println(" ");
        }
    }

    @Test
    public void input4Parallel(){
        String fileName = "input4";
        System.out.println("testing " + fileName);
        String[] args = {fileName + ".dot", "2", "-p", "2"};
        Main.main(args);
        System.out.println(" ");
    }
    @Test
    public void input4(){
        String fileName = "input4";
        System.out.println("testing " + fileName);
        String[] args = {fileName + ".dot", "2"};
        Main.main(args);
        System.out.println(" ");
    }

}