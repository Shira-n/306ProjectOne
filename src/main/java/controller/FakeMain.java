package controller;

public class FakeMain {

    public static void main(String[] args) {
        GUIEntry entry = new GUIEntry(null,"FILE",4, false);
        entry.run();
        Controller controller = entry.getController();
        System.out.print(controller);
    }
}
