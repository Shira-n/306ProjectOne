package controller;

import java.awt.*;

public class FakeMain {

    public static void main(String[] args) {
        System.out.println(new Color((int)(Math.random() * 0x1000000)).toString());
        GUIEntry entry = new GUIEntry(null,"FILE",4, false);
        //entry.run();
        Controller controller = entry.getController();
        System.out.print(controller);


    }
}
