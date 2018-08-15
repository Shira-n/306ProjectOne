package controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColourManager {
    private List<String> _colours = new ArrayList<>();

    List<String> colourChoices = Arrays.asList(
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#f9845b", // orange
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7");  // light gray


    public ColourManager (int numProcessor) {
        for (int i = 0; i < numProcessor; i++) {
            int randomPickIndex  = ((int)(Math.random() * (colourChoices.size()-1)));
            _colours.add(colourChoices.get(randomPickIndex));
            //colourChoices.remove(randomPickIndex);
        }
    }

    public String getColor(int processorIndex) {
        return _colours.get(processorIndex-1);
    }

}
