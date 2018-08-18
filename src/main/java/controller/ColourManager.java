package controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
public class ColourManager {
    private List<String> _colours = new ArrayList<>();


    List<String> colourChoices = new ArrayList<String>() {{
                add("#39add1"), // light blue
                add("#3079ab"), // dark blue
                add("#c25975"), // mauve
                add("#e15258"), // red
                add("#f9845b"), // orange
                add("#838cc7"), // lavender
                add("#7d669e"), // purple
                add("#53bbb4"), // aqua
                add("#51b46d"), // green
                add("#e0ab18"), // mustard
                add("#637a91"), // dark gray
                add("#f092b0"), // pink
                add("#b7c0c7"),//light grey
                add("#FCECC9"),//pale yellow
                add("#C8D5B9"), // pale green
                add("#3B1C32")}
    };

    public ColourManager (int numProcessor) {
        for (int i = 0; i < numProcessor; i++) {
            int randomPickIndex  = (int)(Math.random() * (colourChoices.size()-1));
            _colours.add(colourChoices.get(randomPickIndex));
            System.out.println("Processor " + i + " : " + randomPickIndex + ", "+  _colours.get(i));
            colourChoices.remove(randomPickIndex);
        }
    }

    public String getColor(int processorIndex) {
        //System.out.println("COLOR: " + _colours.get(processorIndex-1));
        return _colours.get(processorIndex-1);
    }

}
